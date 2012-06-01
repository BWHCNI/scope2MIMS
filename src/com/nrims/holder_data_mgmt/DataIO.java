package com.nrims.holder_data_mgmt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import javax.swing.JFileChooser;

/**
 * Class used to deal with input/output of data from/to files.
 * @author fkashem
 */
public class DataIO {

    /*
     * Saves as Holder Data File
     * parameters: location to save to, datapoints
     */
    public static void saveREF(String location, DataPointFileProcessor dpfp) {
        
        HolderDataFile hdf = new HolderDataFile(location, true, dpfp.getRefPointList());
        hdf.writeFileOut();
        hdf.close();
    }

    /*
     * Saves as PRS file
     * parameters: location to save to, datapoints
     */
    public static void savePRS(String location, DataPointFileProcessor dpfp) {
        // Create the reference point list -- This shouldn't be in this method
        dpfp.processTransform();
        RefPointList rpl = dpfp.getRefPointList();

        //These offsets are needed to save the points in the newer Cameca
        //text format correctly.  Without them the points will -NOT- be valid!
        double xoffset = 18.5;
        double yoffset = 0.0;
            File file = new File(location);
            try{
                FileOutputStream out = new FileOutputStream(file);
                Writer bw = new BufferedWriter(new OutputStreamWriter(out));

                int numpts = rpl.getNumRefPoints();
                //number padding should be constant, may need to change
                //with something like:
                //pad = java.lang.Math.round(java.lang.Math.log10((double)numpts));
                long pad = 3;

                //write "header" lines
                bw.write("Version="+"\t"+"200\r\n");
                bw.write("Preset="+numpts+"\t"+"1\r\n");


                for(int i = 0; i < numpts; i++) {
                    RefPoint rp = rpl.getRefPoint(i);
                    int num = i+1;
                    String line = "\"pt"+String.format("%0"+pad+"d", num)+"\"\t";
                    //line += rp.getPointAsCamecaString();

                    double xval = (rp.getXCoord()/1000)+xoffset;
                    double yval = (rp.getYCoord()/1000)+yoffset;
                    double zval = rp.getZCoord();
                    DecimalFormat threeDForm = new DecimalFormat("#.###");

                    line += Double.valueOf(threeDForm.format(xval)) + "\t" +
                            Double.valueOf(threeDForm.format(yval)) + "\t" +
                            java.lang.Math.round(zval) + "\t";
                    //no idea what the -1 is
                    line += "-1\t";
                    line += "\"" + rp.getDateString() + "\"\t";
                    line += "\"" + rp.getComment().trim() + "\"";
                    line += "\r\n";
                    bw.write(line);
                }

                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
