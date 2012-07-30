package eu.scape_project.planning.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class C3POProfileParser {

	private static final String TYPE_OF_OBJECTS_BEGIN = "The collection consists of {1}% '{2}' files. ";

	private static final String TYPE_OF_OBJECTS_SECOND = "It also contains {1}% '{2}' files. ";

	private static final String TYPE_OF_OBJECTS_CONFLICTS = "{1}% files have conflicts. ";

	private static final String TYPE_OF_OBJECTS_UNKNOWN = "{1}% files have an unknown format. ";

	private static final String MISSING = "No format distribution provided";

	private static final Logger log = LoggerFactory.getLogger(C3POProfileParser.class);

	private Document profile;

	public void read(final InputStream stream, boolean validate) {
		ValidatingParserFactory vpf = new ValidatingParserFactory();
		SAXParser parser = null;
		try {
			parser = vpf.getValidatingParser();
		} catch (ParserConfigurationException e) {
			log.error("An error occurred while parsing the c3po profile: {}",
					e.getMessage());
		} catch (SAXException e) {
			log.error("An error occurred while parsing the c3po profile: {}",
					e.getMessage());
		}

		if (validate && !this.isValid(parser, stream)) {
			return; // if validation enabled and not valid
		}

		try {
			final SAXReader reader = new SAXReader();
			this.profile = reader.read(stream);
		} catch (final DocumentException e) {
			log.error("An error occurred while reading the profile: {}",
					e.getMessage());
			this.profile = null;
		}

		try {
			stream.close();
		} catch (final IOException e) {
			log.error("An error occurred while closing the input stream: {}",
					e.getMessage());
		}
	}

	private boolean isValid(SAXParser parser, InputStream stream) {
		log.debug("validating collection profile");
		try {
			SimpleErrorHandler errorHandler = new SimpleErrorHandler();

			SAXReader reader = new SAXReader(parser.getXMLReader());
			reader.setValidation(true);

			reader.setErrorHandler(errorHandler);
			reader.read(stream);

			return errorHandler.isValid();

		} catch (SAXException e) {
			log.error("SAXException: {}", e.getMessage());
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error("DocumentException: {}", e.getMessage());
		} catch (NullPointerException e) {
			log.warn("Factory is not initialized. Did you call init()");
		}

		return false;
	}

	public String getCollectionId() {
		return this.profile.getRootElement().attributeValue("collection");
	}
	
	public String getPartitionFilterKey() {
		return this.profile.getRootElement().element("partition").element("filter").attributeValue("key");
	}

	public String getObjectsCountInPartition() {
		return this.profile.getRootElement().element("partition")
				.attributeValue("count");
	}

	public String getTypeOfObjects() {
		int count = Integer.parseInt(this.getObjectsCountInPartition());
		List<?> properties = this.profile.getRootElement().element("partition")
				.element("properties").elements("property");
		List<?> items = new ArrayList();

		// because xpath expression
		// //property[@id="format"] is not working
		for (Object o : properties) {
			Element prop = (Element) o;
			if (prop.attributeValue("id").equals("format")) {
				items = prop.elements();
				break;
			}
		}

		if (items.isEmpty()) {
			return MISSING;
		}

		StringBuffer response = new StringBuffer();
		String type;
		double tmp;
		double percent;
		if (items.size() >= 1) {
			Element item = (Element) items.remove(0);
			type = item.attributeValue("id");
			tmp = Double.parseDouble(item.attributeValue("value"));
			percent = Math.floor((tmp / count) * 100);
			response.append(TYPE_OF_OBJECTS_BEGIN.replace("{1}", percent + "")
					.replace("{2}", type));
		}

		if (items.size() >= 1) {//already removed first
			Element item = (Element) items.remove(0);
			type = item.attributeValue("id");
			tmp = Double.parseDouble(item.attributeValue("value"));
			percent = Math.floor((tmp / count) * 100);
			response.append(TYPE_OF_OBJECTS_SECOND.replace("{1}", percent + "")
					.replace("{2}", type));
		}

		for (Object o : items) {
			Element e = (Element) o;
			if (e.attributeValue("id").equals("Conflicted")) {
				tmp = Double.parseDouble(e.attributeValue("value"));
				percent = Math.floor((tmp / count) * 100);
				response.append(TYPE_OF_OBJECTS_CONFLICTS.replace("{1}",
						percent + ""));
			} else if (e.attributeValue("id").equals("Unknown")) {
				tmp = Double.parseDouble(e.attributeValue("value"));
				percent = Math.floor((tmp / count) * 100);
				response.append(TYPE_OF_OBJECTS_UNKNOWN.replace("{1}", percent
						+ ""));
			}
		}

		return response.toString();
	}

	private class SimpleErrorHandler implements ErrorHandler {
		private boolean valid;

		public SimpleErrorHandler() {
			this.valid = true;
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			log.error("Error: {}", e.getMessage());
			this.valid = false;
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			log.error("Fatal Error: {}", e.getMessage());
			this.valid = false;

		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			log.error("Warning: {}", e.getMessage());

		}

		public boolean isValid() {
			return this.valid;
		}

	}

}
