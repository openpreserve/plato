/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.tuwien.minireef;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

/**
 * MINImal Registry for the Extensible Evaluation of Formats.
 * This class uses the P2 data to provide a simple format evaluation service.
 * @author Michael Kraxner
 * @author Christoph Becker
 * @author David Tarrant provided the RDF triples from P2, many thanks!
 * @see http://p2-registry.ecs.soton.ac.uk/
 */
public class MiniREEF implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(MiniREEF.class);
	
	
    class RdfFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.toUpperCase().endsWith(".RDF"));
        }
    }
    
    public Model model; 
    /**
     * @param args
     */
    
    public MiniREEF() {
        model = ModelFactory.createMemModelMaker().createDefaultModel();
        
        model.setNsPrefix("w3c", "http://www.w3.org/2001/");
        model.setNsPrefix("pronom", "http://pronom.nationalarchives.gov.uk/#");
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("psl", "http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/");
        model.setNsPrefix("p2-additional", "http://p2-registry.ecs.soton.ac.uk/ontology/#");
//        model.setNsPrefix("risk", "http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/#");
    }
    
    public void addModel(String filename) {
        System.out.println("loading model: "+ filename);
        Model m =  FileManager.get().loadModel(filename);
        model = model.union(m);
    }

    public void addModelFromStream(InputStream in, String base) {
        model = model.read(in, base);
    }
    
    /**
     * adds reasoning ability to the model
     * - best used after all models are loaded
     * HINT: don't forget to call before doing SPARQL queries!  
     */
    public void addReasoning() {
        model = ModelFactory.createInfModel(ReasonerRegistry.getRDFSReasoner() , model);
    }        


    private void addModelsFromDir(File dir) {
        File[] xmls = dir.listFiles(new RdfFilter());
        for (int i = 0; i < xmls.length; i++) {
            addModel(xmls[i].getAbsolutePath());
        }
    }
        
    public Model getModel(){
        return model;
    }
    
    /**
     * loads all models of <param>baseDir</param> subdirectories into MiniREEFs model. 
     * @param baseDir
     */
    public void unifyModels(String baseDir){
        File base = new File(baseDir);
        File[] subs = base.listFiles(new FileFilter() {
                public boolean accept(File f) {
                     return f.isDirectory();
                }
            }); 
        for (int i = 0; i < subs.length; i++) {
            addModelsFromDir(subs[i]);
        }
        model.setNsPrefix("w3c", "http://www.w3.org/2001/");
        model.setNsPrefix("pronom", "http://pronom.nationalarchives.gov.uk/#");
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("psl", "http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/");
        model.setNsPrefix("p2-additional", "http://p2-registry.ecs.soton.ac.uk/ontology/#");
        
        //model.setNsPrefix("risk", "http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/#");

        try {
            model.write(new FileWriter("/home/kraxner/workspace/plato_trunk/data/p2/p2unified.rdf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * performs a query with <param>statement</param> and returns results (as String) mapped to their result variables.
     * It's left to the caller to interpret them correctly
     * 
     * @param statement
     * @return
     */
    public at.tuwien.minireef.ResultSet resolve(String statement) {
        String commonNS = 
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX pronom:<http://pronom.nationalarchives.gov.uk/#>  " +
            "PREFIX p2-additional:<http://p2-registry.ecs.soton.ac.uk/ontology/#>  " +
            "PREFIX risk:<http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/#> ";

        Query query = QueryFactory.create(commonNS + statement, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        at.tuwien.minireef.ResultSet result = new at.tuwien.minireef.ResultSet();
        List<String> cols = new ArrayList<String>();
        
        while ((results != null) && (results.hasNext())) {
            QuerySolution qs = results.next();
            if (cols.size() == 0) {
                // columns are not defined yet
                Iterator<String> varNames = qs.varNames();
                while (varNames.hasNext()) {
                    cols.add(varNames.next());
                }
                result.setColumnNames(cols);
            }
            List<String> row = new ArrayList<String>();
            for (String col: cols) {
                RDFNode node = qs.get(col);
                String value = null;
                if (node.isLiteral()) {
                    // a typed object - return it as string
                    value = node.as(Literal.class).getString();
                } else {
                    // an uri identifying a resource
                    value = node.toString();
                }
                row.add(value);
            }
            try {
                result.addRow(row);
            } catch (IllegalArgumentException e) {
                // "Number of values does not match the number of predefined columns"
                // (this can't happen here, because the columns are based on the actual number of result values) 
                log.error(e.getMessage(),e);
            }
        }
        qe.close();
        return result;
    }
    
    /**
     * simple console application to test SPARQL queries
     *  
     * @param args
     */
    public static void main(String[] args) {
        MiniREEF reef = new MiniREEF();
        String file = "";
        if (args.length > 0) {
            file = args[0];
        }
        if ("".equals(file)) {
            System.out.println("please provide the path to your rdf model");
            return;
        }
        reef.addModel(file);
        reef.addReasoning();
        
        Scanner in = new  Scanner(System.in);
        
        String input;
        do {
            System.out.println(" hit 'q' if you want to quit");
            System.out.println(" hit '1' to say hello to reef");
            System.out.println(" hit '2' to resolve the world");
            System.out.println(" hit '3' resolve more");
            
            input = in.next();
            try {
                if (input.equals("1")) {
                    reef.helloReef();
                } else if (input.equals("2")) {
                    reef.resolveTheWorld();
                } else if (input.equals("3"))  {
                    reef.resolveMore();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(!input.equalsIgnoreCase("q"));
    }
    
    public void resolveTheWorld() {
        String statement=
          "SELECT ?d ?id  WHERE {  " + 
          "?format p2-additional:ubiquity ?u . " +
          "?u rdfs:comment ?d . "  +
          "?format pronom:IsSupertypeOf ?pronomformat . " +
          "?pronomformat pronom:FileFormatIdentifier ?ident ." +
          "?ident pronom:IdentifierType \"PUID\" ." +
          "?ident pronom:Identifier ?id " +
          " }";
        
        at.tuwien.minireef.ResultSet result = resolve(statement);
        System.out.println("resolve the World:");
        
        List<String> cols = result.getColumnNames();
        for(String key : cols) {
            System.out.println(key + "    ");
            
        }
        
        for (int i = 0; i < result.size(); i++) {
            List<String> row = result.getRow(i);
            if (row != null) {
                for (String value : row) {
                    System.out.print(value + "   ");
                }
            }
            System.out.println();
        }
    }
    
    public void resolveMore() {
        String statement = 
            "SELECT ?d  WHERE {  " + 
            "?format p2-additional:documentation_quality ?q . " +
            "?q rdfs:comment ?d . "  +
            "?format pronom:IsSupertypeOf ?pronomformat . " +
            "?pronomformat pronom:FileFormatIdentifier ?ident ." +
//            "?ident pronom:IdentifierType \"PUID\" ." +
//            "?ident pronom:Identifier \"$PUID$\" " +
            "}";

        
/*            
            "SELECT ?d  WHERE {  " + 
      "?format p2-additional:documentation_quality ?q . " +
      "?q rdfs:comment ?d . "  +
      "?format  pronom:FileFormatIdentifier ?ident ." +
      "?ident pronom:IdentifierType \"PUID\" ." +
      "?ident pronom:Identifier \"fmt/18\" " +      
       " }";
        
  */      
        at.tuwien.minireef.ResultSet result = resolve(statement);
        System.out.println("resolve more:");
        
        List<String> cols = result.getColumnNames();
        for(String key : cols) {
            System.out.print(key + "    ");
            
        }
        System.out.println();
        
        for (int i = 0; i < result.size(); i++) {
            List<String> row = result.getRow(i);
            if (row != null) {
                for (String value : row) {
                    System.out.print(value + "   ");
                }
            }
            System.out.println();
        }
    }
    
    public void helloReef() throws Exception{
        System.out.println("hello reef!");
//        String queryString = "SELECT ?s ?id ?name ?disclosure " +
//                             "WHERE { ?s pronom:FormatID ?id ." +
//                             "        OPTIONAL {?s pronom:FormatName ?name } ." +
//                             "        OPTIONAL {?s pronom:FormatDisclosure ?disclosure} }";
        String queryString = "SELECT count(distinct *) " +
        "WHERE { ?x ?y <http://nationalarchives.gov.uk/pronom/Format/728> . " +
        "        ?y rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink> . " +
        "        ?x pronom:SoftwareName  ?swname } ";
        String commonNS = 
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX pronom:<http://pronom.nationalarchives.gov.uk/#> ";
        Query query = QueryFactory.create(commonNS + queryString, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(results);
        qe.close();
        
    }
    
    

}
