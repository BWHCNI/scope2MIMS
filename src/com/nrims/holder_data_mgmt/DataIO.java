package com.nrims.holder_data_mgmt;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import org.apache.commons.io.FilenameUtils;

/**
 * Class used to deal with input/output of data from/to files.
 * @author fkashem
 */
public class DataIO {

    /*
     * Read a .points file into an arraylist of points
     * @param path to points file
     */
    public static ArrayList<DataPoint> readPoints(String location) {
        ArrayList<DataPoint> inPts = new ArrayList<DataPoint>();
        // This block is modified from Transform.java
        try {
            BufferedReader br = new BufferedReader(new FileReader(location));
            String line;
            
            //Counter for point number
            int i = 1;
            while( ( line = br.readLine() ) != null) {
                if(line.equals("STAGE_LIST") || line.equals("UNITS_UM") || line.equals("") ) {
                    continue;
                }
                String[] stringpts = line.split(" ");
                if(stringpts.length != 3) {
                    continue;
                }
                DataPoint nextPoint = new DataPoint(Double.parseDouble(stringpts[0]), Double.parseDouble(stringpts[1]), Double.parseDouble(stringpts[2]), i);
                inPts.add(nextPoint);
                i++;
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
        REFDataFile hdf;

        /* Allowing ref file review if it exists. */
        if ( (new File( location )).exists() ) {
            if ( dpfp == null )
                dpfp = new DataPointFileProcessor();

            // dpfp.setHolderPointFilePath( location );
            ArrayList<REFPoint> rpl = new ArrayList<REFPoint>();

            hdf = new REFDataFile(location, false, rpl);

            hdf.readFileIn();
            hdf.close();
            
            dpfp.setMachinePoints( hdf.getRefPointList() );

        }
    }
    
    /*
     * Saves as Holder Data File. Splits into multiple files if more than 200 points.
     * Add return that gives the state of the save. 
     * parameters: location to save to, datapoints
     */
    public static String saveREF(String location, DataPointFileProcessor dpfp) {
        int points = dpfp.getMachinePoints().size();
        String output = new String();
        
        if(points <= 200) {
            //Check if location has .ref extension. If not, add it.
            if(!FilenameUtils.getExtension(location).equals("ref")) {
                location = location.concat(".ref");
            }
            
            REFDataFile hdf = new REFDataFile(location, true, dpfp.getMachinePoints());
            hdf.writeFileOut();
            hdf.close();
            output = FilenameUtils.getName(location) + " saved.";
        } else {
            output = "Multiple .ref files created: ";
            double divide = points / 200.0;
            
            //Strip extension from location if exists.
            location = FilenameUtils.removeExtension(location);
            
            String newLocation; 
            int start;
            int end;
            
            for(int i = 0; i < divide; i++) {
                
                //Add number to file, add extension.
                newLocation = location.concat(i+".ref");
                
                //Grab the next 200 points or the remaining points
                start = i*200;
                if((start+199) > points-1) {
                    end = points - 1;
                } else {
                    end = start+199;
                }
                
                ArrayList<REFPoint> splitList = new ArrayList<REFPoint>(dpfp.getMachinePoints().subList(start, end));
                
                REFDataFile hdf = new REFDataFile(newLocation, true, splitList);
                hdf.writeFileOut();
                hdf.close();
                output = output.concat(FilenameUtils.getName(newLocation) + " ");
            }
            
        }
        
        return output;
        
    }


    /*
     * Saves as PRS file
     * 
     */
    public static String savePRS(String location, DataPointFileProcessor dpfp) {  
        String output = new String();
        ArrayList<REFPoint> rpl = dpfp.getMachinePoints();
        
        //Check if location passed has the .prs extension, if not add it
        if(!FilenameUtils.getExtension(location).equals("prs")) {
            location = location.concat(".prs");
        }
        
        
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
                    REFPoint rp = rpl.get(i);
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
                output = FilenameUtils.getName(location) + " saved.";
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return output;
    }

}
