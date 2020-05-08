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
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import net.sf.jclec.ExperimentBuilder;
import net.sf.jclec.IAlgorithm;
import net.sf.jclec.IConfigure;

/**
 * The class that executes a MOO experiment in JCLEC-MOEA. The execution can start from a single
 * configuration file or a complete MOO experiment. It is an adaptation of the {@link RunExperiment} class
 * of JCLEC.
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
 * 
 * @see MOExperiment
 * */

public class MOExperimentRunner {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOExperimentRunner(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Execute a MOO experiment.
	 * @param experiment The experiment to be executed.
	 * */
	public void executeSequentially(MOExperiment experiment){
		List<String> configurations = experiment.getConfigurations();
		for(String configuration: configurations){
			executeSequentially(configuration);
		}
	}

	/**
	 * Execute from a single configuration file.
	 * @param configurationFile The name of configuration file to be executed.
	 * */
	public void executeSequentially(String configurationFile){
		ExperimentBuilder builder = new ExperimentBuilder();
		for(String experiment : builder.buildExperiment(configurationFile)){
			experiment = experiment.replace("\\", "/");
			experiment = "experiments"+experiment.substring(experiment.lastIndexOf("/"), experiment.length());
			executeJob(experiment);
		}
	}

	/**
	 * This method checks the existence of the configuration file 
	 * and starts the execution process.
	 * @param jobFilename The name of the configuration file to be loaded.
	 * */
	@SuppressWarnings("unchecked")
	private void executeJob(String jobFilename) {
		// Try open job file
		File jobFile = new File(jobFilename);
		if (jobFile.exists()) {
			try {
				// Job configuration
				XMLConfiguration jobConf = new XMLConfiguration(jobFile);
				// Process header
				String header = "process";
				// Create and configure algorithms
				String aname = jobConf.getString(header+"[@algorithm-type]");
				Class<IAlgorithm> aclass = (Class<IAlgorithm>) Class.forName(aname);
				IAlgorithm algorithm = aclass.getDeclaredConstructor().newInstance();
				// Configure runner
				if (algorithm instanceof IConfigure) {
					((IConfigure) algorithm).configure(jobConf.subset(header));
				}
				// Execute algorithm runner
				algorithm.execute();
			}
			catch (ConfigurationException e) {
				System.out.println("Configuration exception ");
			}			
			catch (Exception e) {
				e.printStackTrace();
			}			
		}
		else {
			System.out.println("Job file not found");
			System.exit(1);			
		}
	}
}
