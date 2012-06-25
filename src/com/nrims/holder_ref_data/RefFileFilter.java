/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filefilter for JFileChooser save dialog. Filters for .ref files.
 * @author fkashem
 */
public class RefFileFilter extends FileFilter
 { 
    public boolean accept(File f)
   {
        if (f.isDirectory())
          {
            return false;
          }

         String s = f.getName();

        return s.toLowerCase().endsWith(".ref");
   }

   public String getDescription() 
  {
       return "*.ref";
  }

}
