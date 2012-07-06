package distance;

import weka.core.Instance;
import weka.core.NormalizableDistance;
import weka.core.neighboursearch.PerformanceStats;

/**
 * Created by IntelliJ IDEA.
 * User: amatic
 * Date: 4/17/12
 * Time: 11:30 PM
 */
public class CosineDistance extends NormalizableDistance {

    public static int ATT_PROTOCOL_TYPE = 4;
    public static int ATT_SERVICE = 5;
    public static int ATT_FLAG = 6;


    @Override
    public String globalInfo() {
        return null;
    }

    @Override
    protected double updateDistance(double v, double v1) {
        return 0;
    }

    public String getRevision() {
        return null;
    }

    @Override
    public double distance(Instance first, Instance second) {


        double distance = 1;

        System.out.println("service: " + first.attribute(ATT_SERVICE).name());
        if (first.value(ATT_PROTOCOL_TYPE) != second.value(ATT_PROTOCOL_TYPE)
                || first.value(ATT_SERVICE) != second.value(ATT_SERVICE)
                || first.value(ATT_FLAG) != second.value(ATT_FLAG)) {

            return distance;
        }

        double xy = 0, xx = 0, yy = 0;

        first.setValue(ATT_PROTOCOL_TYPE, 1);
        second.setValue(ATT_PROTOCOL_TYPE, 1);

        first.setValue(ATT_SERVICE, 1);
        second.setValue(ATT_SERVICE, 1);

        first.setValue(ATT_FLAG, 1);
        second.setValue(ATT_FLAG, 1);

        for (int i = 0; i < first.numAttributes(); i++) {

            xy = xy + first.value(i) * second.value(i);
            xx = xx + first.value(i) * first.value(i);
            yy = yy + second.value(i) * second.value(i);


        }

        return 1 - xy/(xx*yy);
    }

    @Override
    public double distance(Instance first, Instance second, double cutOffValue) {
        return super.distance(first, second, Double.POSITIVE_INFINITY);
    }

    @Override
    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
        return super.distance(first, second, Double.POSITIVE_INFINITY, stats);

    }
}
