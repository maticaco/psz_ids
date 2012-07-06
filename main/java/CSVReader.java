import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: amatic
 * Date: 4/17/12
 * Time: 3:54 PM
 */
public class CSVReader {
    public static Instances load(String path) throws Exception {
        Instances instances = null;
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
        instances = source.getDataSet();
        return instances;
    }

    public static void save(Instances _i,String path) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(_i);
        saver.setFile(new File(path));
        saver.writeBatch();
    }

    //testing purposes
    public static void main(String[] args){
        Instances _i=null;
        try {
            _i = load("./data/output/prepared_set.csv");
            save(_i,"./data/output/prepared_set.arff");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
