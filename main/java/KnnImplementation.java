import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amatic
 * Date: 4/17/12
 * Time: 11:17 PM
 */
public class KnnImplementation {
    static final Logger logger = LoggerFactory.getLogger(KnnImplementation.class);
    private IBk knnClassifier;

    public static Instances removeAttributes(Instances instances, Integer[] remove) throws Exception {
        Remove filter = new Remove();

        StringBuilder list=new StringBuilder();
        for(int _r:remove){
            list.append(Integer.toString(_r)).append(",");
        }

        filter.setAttributeIndices(list.toString().substring(0,list.toString().length()-1));
        filter.setInputFormat(instances);

        return Filter.useFilter(instances, filter);
    }


    public IBk getKnnClassifier() {
        return knnClassifier;
    }


    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("./env/log4j.properties");
        XMLConfiguration configuration =null;
        try {
            configuration=new XMLConfiguration(new File("conf/config.xml"));
        } catch (ConfigurationException e) {
            logger.error("Failed to init configuration",e);
        }
        if (configuration==null)
            return;

        try{
        execute(configuration.subset("euclidianDistance"));
        }catch (Exception e){
            logger.error("Execution failed.",e);
        }
    }

    private static void execute(Configuration configuration) throws Exception {
        Normalize normalize = new Normalize();
        String training_set=configuration.getString("trainSet");
        String validation_set=configuration.getString("dataSet");
        String classifierName=configuration.getString("classifier");
        List<String> removeAtts=configuration.getList("excludeList");
        Integer classifierNum=configuration.getInt("classifierNum");
        String distanceClass=configuration.getString("distanceClass");

        Instances training_data=null,validation_data=null;
        try {
            training_data= CSVReader.load(training_set);
            validation_data= CSVReader.load(validation_set);
        } catch (Exception e) {
            logger.error("Failed to load training set",e);
            System.exit(1);
        }

        if(training_data!=null && validation_data!=null){
            training_data.setClassIndex(training_data.attribute(classifierName).index());
            Integer[] removeIndexes=getIndexesForRemoval(training_data,removeAtts);
            Instances _data=removeAttributes(training_data,removeIndexes);
            normalize.setInputFormat(_data);
            _data = Filter.useFilter(_data, normalize);


            IBk knnClassifier = new IBk(classifierNum);
            try {
                DistanceFunction clazz;
                if(distanceClass!=null && distanceClass.length()>0){
                    clazz= (DistanceFunction) Class.forName(distanceClass).newInstance();
                    knnClassifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(clazz);
                }
                knnClassifier.buildClassifier(_data);
            } catch (Exception e) {
                //todo
            }

            //CSVReader.save(_data,"./data/output/training_set_2000.arff");

            validation_data.setClassIndex(validation_data.attribute(classifierName).index());
            removeIndexes=getIndexesForRemoval(validation_data,removeAtts);
            Instances _v_data=removeAttributes(validation_data,removeIndexes);
            _v_data = Filter.useFilter(_v_data, normalize);
            _v_data.classAttribute().toString();

            //CSVReader.save(_data,"./data/output/val_set_20000.arff");

            if(_v_data.equalHeaders(_data))
                logger.info("Headers equal");
            else
                logger.info("Headers NOT equal");


            Evaluation eval = new Evaluation(_v_data);
            eval.evaluateModel(knnClassifier, _v_data);

            for (int i=0; i< _v_data.numInstances(); i++) {//todo
                System.out.println("class:" +  knnClassifier.classifyInstance(_v_data.instance(i))
                        + "from data set:" + _v_data.instance(i).value(_v_data.instance(i).attribute(43)));
            }

            //System.out.println("error rate: " + eval.errorRate());
            //System.out.println("correct: " + eval.correct());
            //System.out.println("pct correct: " + eval.pctCorrect());
            //System.out.println("pct incorrect: " + eval.pctIncorrect());
            //System.out.println("pct unclasified: " + eval.pctUnclassified());


            System.out.println("class attribute: " + classifierName);
            //System.out.println("index class attribute: " + _v_data.attribute().index());

            //System.out.println("pct true positive: " + eval.weightedTruePositiveRate());
            //System.out.println("pct true negative: " + eval.weightedTrueNegativeRate());

            //System.out.println("pct false positive: " + eval.weightedFalsePositiveRate());
            //System.out.println("pct false negative: " + eval.weightedFalseNegativeRate());








            //Make and normalize train_set, and remove attributes
            //Instances data = removeAttributes(Loader.load(args[0], args[2]), Integer.parseInt(args[4]));
            //data = NormalizeFilter.normalizeInstances(data);

            //make knnEuclidian clasifier from train set
            //KnnEuclidianDistance knnEuclidian = new KnnEuclidianDistance(Integer.parseInt(args[3]), data);


            //Make validation set, and remove attributes
            //Instances validationSet = removeAttributes(Loader.load(args[1], args[2]), Integer.parseInt(args[4]));

            //split validation set to 3 parts
            //Instances validationSetSplit = splitDataSet(3,validationSet, false);

            //normalize new validation set
            //validationSet = NormalizeFilter.normalizeInstances(validationSet);
            //validationSet = NormalizeFilter.normalizeInstances(validationSetSplit);

            //evaluate model
            //Evaluation eval = new Evaluation(validationSet);
            //eval.evaluateModel(knnEuclidian.getKnnClasifier(), validationSet);

            //for (int i=0; i< validationSet.size(); i++) {
            //System.out.println("class:" +  knnEuclidian.getKnnClasifier().classifyInstance(validationSet.instance(i))
            // + "from data set:" + validationSet.instance(i).value(validationSet.instance(i).attribute(43)));
            //}

//        System.out.println("error rate: " + eval.errorRate());
//        System.out.println("correct: " + eval.correct());
//        System.out.println("pct correct: " + eval.pctCorrect());
//        System.out.println("pct incorrect: " + eval.pctIncorrect());
//        System.out.println("pct unclasified: " + eval.pctUnclassified());
//
//
//        System.out.println("class attribute: " + args[2]);
//        System.out.println("index class atributa: " + validationSet.attribute(args[2]).index());
//
//        System.out.println("pct true positive: " + eval.weightedTruePositiveRate());
//        System.out.println("pct true negative: " + eval.weightedTrueNegativeRate());
//
//        System.out.println("pct false positive: " + eval.weightedFalsePositiveRate());
//        System.out.println("pct false negative: " + eval.weightedFalseNegativeRate());

        }


    }

    private static Integer[] getIndexesForRemoval(Instances data, List<String> removeAtts) {
        Integer[] result=new Integer[removeAtts.size()];
        for(int _i=0;_i<removeAtts.size();_i++){
            String _attribute=removeAtts.get(_i);
            Attribute attribute = data.attribute(_attribute);
            result[_i]=attribute.index()+1;
        }

        return result;
    }
}
