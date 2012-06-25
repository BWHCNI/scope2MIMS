/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nrims.holder_ref_data;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filefilter for jfilechooser save dialog. Filters for .prs files.
 * @author fkashem
 */

class PrsFileFilter extends FileFilter
 { 
    public boolean accept(File f)
   {
        if (f.isDirectory())
          {
            return false;
          }

         String s = f.getName();

        return s.toLowerCase().endsWith(".prs");
   }

   public String getDescription() 
  {
       return "*.prs";
  }

}
