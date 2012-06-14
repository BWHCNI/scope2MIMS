package com.nrims.holder_data_mgmt;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 * Class used to deal with input/output of data from/to files.
 * @author fkashem
 */
public class DataIO {

    /*
     * Read a .points file into an arraylist of points
     * @param path to points file
     */
    public static ArrayList<Point> readPoints(String location) {
        ArrayList<Point> inPts = new ArrayList<Point>();
        // This block is modified from Transform.java
        try {
            BufferedReader br = new BufferedReader(new FileReader(location));
            String line;
            
            while( ( line = br.readLine() ) != null) {
                if(line.equals("STAGE_LIST") || line.equals("UNITS_UM") || line.equals("") ) {
                    continue;
                }
                String[] stringpts = line.split(" ");
                if(stringpts.length != 3) {
                    continue;
                }
                Point nextPoint = new Point(Double.parseDouble(stringpts[0]), Double.parseDouble(stringpts[1]), Double.parseDouble(stringpts[2]));
                inPts.add(nextPoint);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return inPts;
    }
        
    /*
     * Method needs fixing, copied over from Holder_Ref_Data_View
     * May need to re-add a field for the "Holder Point" filepath in the dpfp class.
     */
    public static void openREF(String location, DataPointFileProcessor dpfp) {
        HolderDataFile hdf;

        /* Allowing ref file review if it exists. */
        if ( (new File( location )).exists() ) {
            if ( dpfp == null )
                dpfp = new DataPointFileProcessor();

            // dpfp.setHolderPointFilePath( location );
            ArrayList<RefPoint> rpl = new ArrayList<RefPoint>();

            hdf = new HolderDataFile(location, false, rpl);

            hdf.readFileIn();
            hdf.close();
            
            dpfp.setDestPoints( hdf.getRefPointList() );

        }
    }
    
    /*
     * Saves as Holder Data File
     * parameters: location to save to, datapoints
     */
    public static void saveREF(String location, DataPointFileProcessor dpfp) {
        //TODO: Insert check that transform has occurred
        
        HolderDataFile hdf = new HolderDataFile(location, true, dpfp.getDestPoints());
        hdf.writeFileOut();
        hdf.close();
    }


    /*
     * Saves as PRS file
     * 
     */
    public static void savePRS(String location, DataPointFileProcessor dpfp) {
        //TODO: Insert check that transform has occurred.
        
        ArrayList<RefPoint> rpl = dpfp.getDestPoints();
        //These offsets are needed to save the points in the newer Cameca
        //text format correctly.  Without them the points will -NOT- be valid!
        double xoffset = 18.5;
        double yoffset = 0.0;
            File file = new File(location);
            try{
                FileOutputStream out = new FileOutputStream(file);
                Writer bw = new BufferedWriter(new OutputStreamWriter(out));

                int numpts = rpl.size();
                //number padding should be constant, may need to change
                //with something like:
                //pad = java.lang.Math.round(java.lang.Math.log10((double)numpts));
                long pad = 3;

                //write "header" lines
                bw.write("Version="+"\t"+"200\r\n");
                bw.write("Preset="+numpts+"\t"+"1\r\n");


                for(int i = 0; i < numpts; i++) {
                    RefPoint rp = rpl.get(i);
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
