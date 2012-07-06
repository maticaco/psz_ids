import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: amatic
 * Date: 4/17/12
 * Time: 10:30 AM
 */
public class DataParser {

    static final Logger logger = LoggerFactory.getLogger(DataParser.class);

    public static final Character IN_SEPARATOR = ',';
    public static final Character OUT_SEPARATOR = ',';

    public static final Map<String,DataParser.AttackType> attackMap = new HashMap<String,DataParser.AttackType>();


    public static final String inputPath="dataPrepare.input.path";
    public static final String outPath="dataPrepare.output.path";

    /*additional columns */
    private static final String[] addColumn = new String[]{"key","is_attack","attack_group"};

    public static void main(String args[]) throws IOException {
        XMLConfiguration configuration =null;
        try {
            configuration=new XMLConfiguration(new File("conf/config.xml"));
        } catch (ConfigurationException e) {
            logger.error("Failed to init configuration",e);
        }
        if (configuration==null)
            return;

        prepareData(configuration);

    }

    private static int prepareData(XMLConfiguration config) throws IOException {
        int recCount=0;
        int cntDos = 0;
        int cntR2L = 0;
        int cntU2R = 0;
        int cntProbe = 0;
        int cntOther = 0;
        int cntNoAttack = 0;
        CSVReader reader=null;
        CSVWriter writer=null;


        try{
            String iPath=config.getString(inputPath);
            String oPath=config.getString(outPath);

            reader = new CSVReader(new FileReader(iPath),IN_SEPARATOR);
            writer = new CSVWriter(new BufferedWriter(new FileWriter(oPath)),OUT_SEPARATOR);

            String[] _nextLine=reader.readNext();
            ArrayList<String> header=new ArrayList<String>(_nextLine.length),headerOut=new ArrayList<String>(_nextLine.length+addColumn.length);
            Collections.addAll(header, _nextLine);
            Collections.addAll(headerOut, _nextLine);


            int at_index=-1;
            for(int _i=0;_i<header.size();_i++){
                if(header.get(_i).equalsIgnoreCase("attack_type"))
                    at_index=_i;
            }

            headerOut.addAll(Arrays.asList(addColumn));

            writer.writeNext(headerOut.toArray(new String[1]));

            while ((_nextLine = reader.readNext()) != null) {
                String attackType=_nextLine[at_index];
                ArrayList<String> _line=new ArrayList<String>(_nextLine.length);
                Collections.addAll(_line, _nextLine);

                recCount++;

                AttackType _aType=attackMap.get(attackType);

                if(_aType!=null){
                    switch (_aType) {
                        case DOS:
                            cntDos++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.DOS.toString());
                            break;
                        case U2R:
                            cntU2R++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.U2R.toString());
                            break;
                        case R2L:
                            cntR2L++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.R2L.toString());
                            break;
                        case probe:
                            cntProbe++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.probe.toString());
                            break;
                        case other:
                            cntOther++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.other.toString());
                            break;
                        case normal:
                            cntNoAttack++;
                            _line.add(Integer.toString(recCount));
                            _line.add("0");
                            _line.add(AttackType.normal.toString());
                            break;
                        default:
                            cntOther++;
                            _line.add(Integer.toString(recCount));
                            _line.add("1");
                            _line.add(AttackType.other.toString());
                            break;



                    }
                }else{
                    logger.info("Not recognized type for: {}",attackType);
                    cntOther++;
                    _line.add(Integer.toString(recCount));
                    _line.add("1");
                    _line.add(AttackType.other.toString());
                }

                writer.writeNext(_line.toArray(new String[1]));
                writer.flush();
            }

        }finally {
            if(reader!=null)
                reader.close();
            if(writer!=null)
                writer.close();
        }

        System.out.println("Num records: " + (recCount -1));
        System.out.println("===========================");

        System.out.println("Dos attack records: " + cntDos);
        System.out.println("R2L attack records: " + cntR2L);
        System.out.println("U2R attack records: " + cntU2R);
        System.out.println("Probe attack records: " + cntProbe);
        System.out.println("Probe other records: " + cntOther);
        System.out.println("NO attack records: " + cntNoAttack);


        return recCount;
    }

    public enum AttackType {

        DOS,
        U2R,
        R2L,
        probe,
        other,
        normal

    }

    static{
        System.setProperty("log4j.configuration","./env/log4j.properties");
        PropertyConfigurator.configure("./env/log4j.properties");


        attackMap.put("back.",AttackType.DOS);
        attackMap.put("land.",AttackType.DOS);
        attackMap.put("neptune.",AttackType.DOS);
        attackMap.put("pod.",AttackType.DOS);
        attackMap.put("smurf.",AttackType.DOS);
        attackMap.put("teardrop.",AttackType.DOS);

        attackMap.put("buffer_overflow.",AttackType.U2R);
        attackMap.put("loadmodule.",AttackType.U2R);
        attackMap.put("perl.",AttackType.U2R);
        attackMap.put("rootkit.",AttackType.U2R);

        attackMap.put("ftp_write.",AttackType.R2L);
        attackMap.put("guess_passwd.",AttackType.R2L);
        attackMap.put("imap.",AttackType.R2L);
        attackMap.put("multihop.",AttackType.R2L);
        attackMap.put("phf.",AttackType.R2L);
        attackMap.put("spy.",AttackType.R2L);
        attackMap.put("warezclient.",AttackType.R2L);
        attackMap.put("warezmaster.",AttackType.R2L);


        attackMap.put("ipsweep.",AttackType.probe);
        attackMap.put("nmap.",AttackType.probe);
        attackMap.put("portsweep.",AttackType.probe);
        attackMap.put("satan.",AttackType.probe);

        attackMap.put("normal.",AttackType.normal);

        attackMap.put("default.",AttackType.other);

    }
}
