package com.nrims.holder_data;

import com.nrims.holder_ref_data.CoeffData;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import org.apache.commons.io.FilenameUtils;
import au.com.bytecode.opencsv.*;

/**
 * Class used to deal with input/output of data from/to files.
 * @author fkashem
 */
public class DataIO {
    private String coeffFilePath;
    private String srcFilePath;
    private String destFilePath;
    private DataPointFileProcessor data;

    public DataIO(DataPointFileProcessor dp) {
        data = dp;
    }
    
    public String getCoeffFilePath() {
        return coeffFilePath;
    }
    
    public String getSrcFilePath() {
        return srcFilePath;
    }
    
    public String setDestFilePath() {
        return destFilePath;
    }
    
    
    public String readSource(String location) {
        String output = new String();
        
        //Check that file exists.
        if(!(new File(location)).exists()) {
            return "File does not exist. Try again";
        }
        
        //check if .points or .ref, call appropriate method
        if(FilenameUtils.isExtension(location,"points")) {
            output = readPoints(location);
        } else if(FilenameUtils.isExtension(location,"ref")) {
            output = readREF(location);
        } else {
            return "Cannot open file. Point coord files need to have a .points or .ref extension to be opened.";
        }
        
        return output;
    }
    
    
    /*
     * Read a .points file into an arraylist of points
     * @param path to points file
     */
    public String readPoints(String location) {
        ArrayList<DataPoint> inPts = new ArrayList<DataPoint>();
        String output = new String();

        
        //TODO Add check if file exists or not, have proper exception handling.
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
            
            //Set srcFilePath field
            srcFilePath = location;
            data.setSrcPoints(inPts);
            output = "Source points read from " + FilenameUtils.getName(location);
        
        } catch (FileNotFoundException e){
            output = "File not found.";
        } catch(IOException e) {
            e.printStackTrace();
        } 
        
        return output;
    }
        
    /*
     * Method loads points from an REF file into the source points of the application.
     * @param location - path to a .ref File which exists (check done in open method)
     */
    public String readREF(String location) {
        REFDataFile hdf;
        String output = new String();

            
        ArrayList<REFPoint> rpl = new ArrayList<REFPoint>();

        hdf = new REFDataFile(location, false, rpl);

        output = hdf.readFileIn();
        hdf.close();

        data.setMachinePoints( hdf.getRefPointList() );
        srcFilePath = location;
        output = output.concat("Source points read from " + FilenameUtils.getName(location));
        
        
        return output;
    }
    
    /*
     * Saves as Holder Data File. Splits into multiple files if more than 200 points.
     * Add return that gives the state of the save. 
     * parameters: location to save to, datapoints
     */
    public String saveREF(String location, ArrayList<REFPoint> rpl) {
        int points = rpl.size();
        String output = new String();
        
        if(points <= 200) {
            //Check if location has .ref extension. If not, add it.
            if(!FilenameUtils.getExtension(location).equals("ref")) {
                location = location.concat(".ref");
            }
            
            REFDataFile hdf = new REFDataFile(location, true, rpl);
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
                
                ArrayList<REFPoint> splitList = new ArrayList<REFPoint>(data.getMachinePoints().subList(start, end));
                
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
    public String savePRS(String location, ArrayList<REFPoint> rpl) {  
        String output = new String();
        
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
                output = "Save unsuccessful.";
                e.printStackTrace();
            }
            
            return output;
    }
    
    /*
     * Save coefficient .txt file. Input: 4x4 array for x and y coeffs
     * Format of file: 32 lines. Xcol1, xcol2, xcol3, xcol4, ycol1, ycol2, ycol3, ycol4
     */
    public String saveCoeff(String location, CoeffData data) {
        String output = new String();
        double[][] xCoeff = data.getXCoefficients();
        double[][] yCoeff = data.getYCoefficients();
        
        //Make sure coeffs passed are the right dimensions. 
        if( xCoeff.length != 4 || yCoeff[0].length != 4) {
            output = "Incorrect coefficients passed. Coefficients should be in a 4x4 matrix.";
            return output;
        } else {
            for(int i = 0; i < 4; i++) {
                if(xCoeff[i].length != 4 || yCoeff[i].length != 4) {
                    output = "Incorrect coefficients passed. Coefficients should be in a 4x4 matrix.";
                    return output;
                }
            }
        }
        
        // Remove file extension if present, then add _coefficients.txt
        location = FilenameUtils.removeExtension(location);
        location = location.concat("_coefficients.txt");
        
        File file = new File(location);
        try {
            FileOutputStream out = new FileOutputStream(file);
            Writer bw = new BufferedWriter(new OutputStreamWriter(out));
            
            String line = new String();
            
            //Write all the x coeffs.
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    line = Double.toString(xCoeff[j][i]);
                    line = line.concat("\n");
                    bw.write(line);
                }
            }
            
            //Write all the y coeffs
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    line = Double.toString(yCoeff[j][i]);
                    line = line.concat("\n");
                    bw.write(line);
                }
            }
            
            bw.close();
            output = FilenameUtils.getName(location) + " saved.";
            
        } catch (Exception e) {
                output = "Save unsuccessful.";
                e.printStackTrace();
        }
        
        return output;
    }
    
    /*
     * Saves the information used to compute the coefficients. 
     * Name will be: name_coefficientComputation.csv
     */
    public String saveCoeffComputation(String location, CoeffData data) {
        String output = new String();
        
        //Remove extension, add suffix.
        location = FilenameUtils.removeExtension(location);
        location = location.concat("_coefficientComputation.csv");
        
        File file = new File(location);
        try {
            FileOutputStream out = new FileOutputStream(file);
            CSVWriter writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out)), ',');
            
            //Each line needs to be a String[]
            //Write headers first.
            String[] splitLine = "Point#Nikon X#Nikon Y#Found X#Found Y#Calculated X#Calculated Y#error".split("#");
            writer.writeNext(splitLine);
            
            //Now write the data.
            DataPoint current;
            double[] currentCalc;
            for(int i = 0; i < data.getRefPoints().size(); i++) {
                currentCalc = data.getCalculatedPoints().get(i);
                current = data.getRefPoints().get(i);
                String line = current.getNum() + "#" + Double.toString(current.getXCoord()) + "#" + Double.toString(current.getYCoord()) + "#" +
                        Double.toString(current.getXFound()) + "#" + Double.toString(current.getYFound()) + "#" + Double.toString(currentCalc[0]) + "#" +
                        Double.toString(currentCalc[1]) + "#" + data.getError()[i];
                splitLine = line.split("#");
                writer.writeNext(splitLine);
                
            }
            
            writer.close();
            output = FilenameUtils.getName(location) + " saved.";
            
        } catch (Exception e) {
                output = "Save unsuccessful.";
                e.printStackTrace();
        }       
        
        return output;
    }
    

    
    /*
     * Open a coefficients file and place into a double[]
     * Method taken from transform.java but with checks that numbers are being read 
     * in.
     */
    public double[] readCoeff(String location) {
        double[] coeff = new double[32];
        try {
            BufferedReader br = new BufferedReader(new FileReader(location));
            String line;
            for(int i =0; i<32; i++) {
                if ((line = br.readLine()) != null) {
                    coeff[i] = Double.parseDouble(line);
                    
                    //do a check that all 32 lines read are numbers
                    if (Double.isNaN(coeff[i])) {
                        coeff = new double[0];
                        return coeff;
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        
        return coeff;
        
    }
    
}