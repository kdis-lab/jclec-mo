/*
This file belongs to JCLEC-MO, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018.  A. Ramirez, J.R. Romero, S. Ventura.
Knowledge Discovery and Intelligent Systems Research Group.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package net.sf.jclec.mo.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a MOO experiment. An experiment is comprised of one of more configuration
 * files that will be executed by JCLEC-MOEA. This class facilitates the management of configuration
 * files, thus allowing adding and removing them. 
 * 
 * <p>HISTORY:
 * <ul>
 * 	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 *  
 * @version 1.0 
 *  
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 * </p>
 * */

public class MOExperiment {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The set of configuration files comprising the experiment */
	protected List<String> configurations;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOExperiment(){
		super();
		this.configurations = new ArrayList<String>();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Add a new experiment.
	 * @param configuration The name of the configuration file.
	 * */
	public void addConfiguration(String configuration){
		this.configurations.add(configuration);
	}

	/**
	 * Add a new configuration.
	 * @param configuration The name of the configuration file.
	 * */
	public void addConfigurations(List<String> configurations){
		this.configurations.addAll(configurations);
	}

	/**
	 * Add the set of configurations saved in a given directory.
	 * @param diname The name of the directory containing the configuration files.
	 * */
	public void addConfigurationsFromDirectory(String dirname){
		File directory = new File(dirname);
		File [] files;
		if(directory.exists() && directory.isDirectory()){
			files = directory.listFiles();
			for(int i=0; i<files.length; i++){
				this.configurations.add(files[i].getAbsolutePath());
			}
		}
	}

	/**
	 * Remove a configuration.
	 * @param configuration The name of the configuration file to be removed.
	 * */
	public void removeConfiguration(String configuration){
		this.configurations.remove(configuration);
	}

	/**
	 * Remove a configuration.
	 * @param index The position index of the configuration file to be removed.
	 * */
	public void removeConfiguration(int index){
		this.configurations.remove(index);
	}

	/**
	 * Set a configuration in a given position.
	 * @param index The position index.
	 * @param configuration The name of the configuration file.
	 * */
	public void setConfiguration(int index, String configuration){
		if(index<this.configurations.size())
			this.configurations.set(index, configuration);
	}

	/**
	 * Get all the configurations.
	 * @return A list containing the names of all the configurations files.
	 * */
	public List<String> getConfigurations(){
		return this.configurations;
	}

	/**
	 * Get the configuration at a given position.
	 * @param index The position index.
	 * @return The name of the configuration file at the specified index. Null if the
	 * index is not a valid position.
	 * */
	public String getConfiguration(int index){
		if(index>=0 && index<this.configurations.size()){
			return this.configurations.get(index);
		}
		return null;
	}

	/**
	 * Get the number of configurations.
	 * @return The number of configurations currently stored.
	 * */
	public int getNumberOfConfigurations(){
		return this.configurations.size();
	}		
}
