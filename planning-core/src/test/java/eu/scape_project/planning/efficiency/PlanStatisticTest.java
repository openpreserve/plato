package eu.scape_project.planning.efficiency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.xml.PlanParser;

public class PlanStatisticTest {

    @Test
    public void testSinglePlanFromFile() throws PlatoException, IOException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-minimal.xml");

        File userDir = FileUtils.getUserDirectory();
        File outFile = new File(userDir, "statistics.csv");
        Writer writer = new FileWriter(outFile);
        PlanStatisticsGenerator statistics = new PlanStatisticsGenerator(writer, null);

        List<Plan> plans = parser.importProjects(in);
        for (Iterator<Plan> iter = plans.iterator(); iter.hasNext();) {
            statistics.writeStatistics(iter.next());
        }
        statistics.endReport();
        System.out.printf("Statistics written to : %s", outFile.getAbsolutePath());
    }

    @Test
    public void testStageSinglePlanFromFile() throws PlatoException, IOException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/Digital_Preservation_of_Video_Games_DOS.xml");

        File userDir = FileUtils.getUserDirectory();
        File outFile = new File(userDir, "stage-statistics.csv");
        Writer writer = new FileWriter(outFile);
        StateChangeLogGenerator statistics = new StateChangeLogGenerator(writer, null);

        List<Plan> plans = parser.importProjects(in);
        for (Iterator<Plan> iter = plans.iterator(); iter.hasNext();) {
            statistics.writeStatistics(iter.next());
        }
        statistics.endReport();
        System.out.printf("Statistics written to : %s", outFile.getAbsolutePath());
    }

    protected EntityManager newConnection() {

        // <property name="hibernate.archive.autodetection" value="class, hbm"
        // />
        // <property name="hibernate.connection.driver_class"
        // value="com.mysql.jdbc.Driver" />
        // <property name="hibernate.connection.url"
        // value="jdbc:mysql://localhost:3306/platodbtest?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8&amp;characterEncoding=UTF-8"
        // />
        // <property name="hibernate.connection.username" value="platotest" />
        // <property name="hibernate.connection.password" value="platotest" />
        //
        // <property name="hibernate.hbm2ddl.auto" value="create" />
        // <property name="hibernate.dialect"
        // value="org.hibernate.dialect.MySQL5InnoDBDialect" />
        // <property name="hibernate.show_sql" value="false" />
        // <property name="hibernate.format_sql" value="true" />

        // check order with new connection, this time don't recreate schema
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties
            .put(
                "hibernate.connection.url",
                "jdbc:mysql://localhost:3306/platodbtest?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8&amp;characterEncoding=UTF-8");
        properties.put("hibernate.connection.username", "plato");
        properties.put("hibernate.connection.password", "xxxx");

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase", properties);
        return emFactory.createEntityManager();
    }

    @Ignore
    @Test
    public void testGenerateStatisticFromDB() throws IOException {
        File userDir = FileUtils.getUserDirectory();
        File outFile = new File(userDir, "statistics.csv");
        System.out.printf("Statistics are written to : %s ...", outFile.getAbsolutePath());

        Writer writer = new FileWriter(outFile);
        PlanStatisticsGenerator statistics = new PlanStatisticsGenerator(writer, newConnection());
        statistics.writeCompleteStatistics();

        System.out.printf("finished writing statistics to %s", outFile.getAbsolutePath());

    }

}
