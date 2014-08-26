package eu.scape_project.planning.efficiency;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import eu.scape_project.planning.model.Plan;

public abstract class StatisticsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsGenerator.class);

    protected EntityManager em;
    protected ICsvBeanWriter listWriter;

    private final List<String> headerList = new ArrayList<String>(65);
    private final List<CellProcessor> processorList = new ArrayList<CellProcessor>(65);

    protected String[] headers = new String[] {};
    protected CellProcessor[] processors = new CellProcessor[] {};

    public StatisticsGenerator(Writer writer, EntityManager em) throws IOException {
        this.em = em;

        listWriter = new CsvBeanWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    }

    public abstract void writeStatistics(Plan plan) throws IOException;

    protected abstract void setupColumns();

    /**
     * Finalizes the report
     * 
     * @throws IOException
     */
    public void endReport() throws IOException {
        listWriter.close();
    }

    /**
     * Iterates over all plans in the database and writes their statistics one
     * by one.
     * 
     * @throws IOException
     */
    public void writeCompleteStatistics() throws IOException {
        try {
            Query qry = em.createQuery("select p from Plan p", Plan.class);
            int step = 5;
            int first = 0;
            qry.setMaxResults(step);
            List<Plan> plans = null;
            do {
                qry.setFirstResult(first);
                plans = qry.getResultList();
                for (Iterator<Plan> iter = plans.iterator(); iter.hasNext();) {
                    Plan p = iter.next();
                    if (p != null) {
                        try {
                            writeStatistics(p);
                        } catch (Exception e) {
                            LOGGER.error("failed to generate statistic for: " + p.getId(), e);
                        }
                    }
                }
                first += step;
            } while ((plans != null) && (!plans.isEmpty()));

            endReport();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void addColumn(String field, CellProcessor processor) {
        headerList.add(field);
        processorList.add(processor);
    }

    protected void finishColumns() {
        headers = headerList.toArray(headers);
        processors = processorList.toArray(processors);
    }

}
