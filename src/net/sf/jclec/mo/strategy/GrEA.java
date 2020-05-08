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

package net.sf.jclec.mo.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.IValueFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.MaxObjectiveValue;
import net.sf.jclec.mo.command.MinObjectiveValue;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.EpsilonDominanceComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.IGrEAMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.util.GridObjectiveProperties;
import net.sf.jclec.mo.strategy.util.Hypercube;
import net.sf.jclec.util.random.IRandGen;

/**
 * GrEA strategy
 * 
 * <p>The <b>Gr</b>id based <b>E</b>volutionary <b>A</b>lgorithm 
 * proposes the partition of the objective space into grids, which
 * are adaptively constructed considering the objectives values
 * in the current population. Inspired by NSGA-II, it also ranks
 * the population by fronts in the environmental selection phase.
 * Crowding distance and metrics for measure the spread of solutions
 * are based on grid properties. A grid-dominance relation is also
 * established. All these metrics are used in both the mating selection
 * and environmental selection methods. GrEA does not consider an 
 * archive of solutions, so non-dominated solutions should be 
 * extracted from the final population.</p>
 * 
 * <p>As precondition, all objectives functions should be maximized
 * or minimized, combining both is not allowed.</p>
 * 
 * <p><i>Paper</i>: S. Yang, M. Li, X. Liu, and J. Zheng, “A Grid-Based
 * Evolutionary Algorithm for Many-Objective Optimization”, IEEE Transactions
 * on Evolutionary Computation, vol. 17, no. 5, pp. 721–736, 2013.</p>
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 *  
 * @see MOStrategy
 * */
public class GrEA extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 2782143450106889893L;

	/** The number of division of the objective space in each
	 * dimension for constructing the grid */
	private int numberOfDivisions;

	/** List that stores the space division properties for each objective */
	protected List<GridObjectiveProperties> spaceDivProperties;

	/** Hypercubes comparator */
	protected EpsilonDominanceComparator hComparator;
	
	/** Command to calculate minimum objective values */
	protected MinObjectiveValue commandMinValues;
	
	/** Command to calculate maximum objective values */
	protected MaxObjectiveValue commandMaxValues;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public GrEA(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the number of divisions of the
	 * objective space.
	 * @return The number of divisions
	 */
	public int getNumberOfDivisions() {
		return numberOfDivisions;
	}

	/**
	 * Set the number of divisions of the
	 * objective space.
	 * @param div The number of divisions to be set
	 */
	protected void setNumberOfDivisions(int div) {
		this.numberOfDivisions = div;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for GrEA are:
	 * <ul>
	 * 	<li>div (<code>integer</code>): <p>Number of grids</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){
		
		// Call super configuration
		super.configure(settings);
		
		int div = settings.getInt("div");
		setNumberOfDivisions(div);

		// All objectives should be maximized or minimized
		if(isMaximized()==null){
			System.err.println("All objectives should be maximized or minimized");
			System.exit(-1);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA initialize internal variables and create
	 * the hypercube comparator.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		
		// Initialize the properties and auxiliary commands
		this.spaceDivProperties = new ArrayList<GridObjectiveProperties>();
		this.commandMaxValues = new MaxObjectiveValue();
		this.commandMinValues = new MinObjectiveValue();
		
		// Create the comparator of hypercubes
		createHypercubeComparator();

		// the archive is not used
		return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		this.spaceDivProperties.clear();
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA uses a Pareto comparator.</p>
	 * */
	@Override
	public void createSolutionComparator(
			Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA uses a tournament-based selection method based on Pareto
	 * dominance and grid dominance. Before the mating selection, it
	 * sets the grid boundaries and grid metrics for the current
	 * population.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		// Set space division properties
		setGridBoundaries(population);

		// Initialize the specific properties of the fitness object
		for(int i=0; i<population.size(); i++){
			((IGrEAMOFitness)population.get(i).getFitness()).setFront(-1);
			((IGrEAMOFitness)population.get(i).getFitness()).setDominatedBy(0);
		}

		// Set grids
		gridAssignment(population);

		// Fitness assignment, it will complete the grid 
		// properties in the current set
		fitnessAssignment(population,null);

		// Mating selection based on tournament
		List<IIndividual> parents = tournamentSelection(population); 
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA sorts the population in fronts as NSGA-II.
	 * Then, some individuals from the critical
	 * front are selected after comparing their grid
	 * metrics and adjust them properly.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, List<IIndividual> offspring, List<IIndividual> archive) {

		// Join the populations
		ArrayList<IIndividual> allInds = new ArrayList<IIndividual>();
		allInds.addAll(population);

		for(IIndividual ind: offspring){
			if(!allInds.contains(ind))
				allInds.add(ind);
		}

		// Initialize the specific properties of the fitness
		for(int i=0; i<allInds.size(); i++){
			((IGrEAMOFitness)allInds.get(i).getFitness()).setFront(-1);
			((IGrEAMOFitness)allInds.get(i).getFitness()).setDominatedBy(0);
		}

		// Call the non-dominated sorting method in NSGA-II
		NSGA2 nsga2 = new NSGA2();
		nsga2.setSolutionComparator(getSolutionComparator());
		List<List<IIndividual>> populationByFronts = nsga2.fastNonDominatedSorting(allInds);

		// Select the individuals from the better fronts until
		// the current front can not be added completely (critical front)
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		int size =  population.size();
		int index = 0;
		while((survivors.size() + populationByFronts.get(index).size()) < size){
			survivors.addAll(populationByFronts.get(index));
			index++;
		}
		
		// The number of individuals to be selected from the critical front
		int fill = size-survivors.size();

		if(fill>0){
			List<IIndividual> selectedFront = populationByFronts.get(index);
			// Select some individuals
			if(fill!=selectedFront.size()){
				// Update space division properties and set grids
				this.spaceDivProperties.clear();
				setGridBoundaries(selectedFront);
				gridAssignment(selectedFront);

				// Initialization of grid metrics
				initializeGridFront(selectedFront);

				// Select best individuals from the critical front
				int added = 0;
				do{
					// Find best individual using grid metrics
					IIndividual best = findBest(selectedFront);

					// Add individual to new set
					survivors.add(best.copy());
					added++;

					// Remove best from the front
					selectedFront.remove(best);

					// Add penalties to similar individuals
					// GCD adjustment
					gridCrowdingDistanceAdjustment(selectedFront, best);

					// GR adjustment
					gridRankingAdjustment(selectedFront, best);
				}while(added!=fill);
			}
			// Add all individuals
			else{
				survivors.addAll(selectedFront);
			}
		}
		// Return the new population
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA does not define an archive, 
	 * so <code>null</code> is returned by default.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, List<IIndividual> offspring, List<IIndividual> archive) {
		// GrEA does not use archive
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>GrEA calculates grid properties: ranking, crowding
	 * distance and point distance.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		int size = population.size();
		double gridRanking, gridCrowdingDist, gridPointDist;
		IIndividual ind;
		IGrEAMOFitness fitness;
		Hypercube grid;

		for(int i=0; i<size; i++){
			ind = population.get(i);
			fitness = (IGrEAMOFitness)ind.getFitness();
			grid = fitness.getHypercube();

			// Grid ranking
			gridRanking = computeGridRanking(grid);
			fitness.setRanking(gridRanking);

			// Grid crowding distance
			gridCrowdingDist = computeGridCrowdingDistance(grid, population);
			fitness.setCrowdingDistance(gridCrowdingDist);

			// Grid point coordinate point distance
			gridPointDist = computeGridPointDistance(ind, grid);
			fitness.setCoordinatePointDistance(gridPointDist);
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compute the adaptive characteristics of the grids:
	 * lower and upper boundaries and width of each
	 * objective for the given population.
	 * @param population Individuals in the population
	 * */
	protected void setGridBoundaries(List<IIndividual> population){
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double min, max, lower, upper, width, aux, div = getNumberOfDivisions();

		this.commandMinValues.setPopulation(population);
		this.commandMaxValues.setPopulation(population);
		
		// For each objective
		for(int i=0; i<nObj; i++){

			// Get minimum and maximum values for this objective
			this.commandMinValues.setObjectiveIndex(i);
			this.commandMinValues.execute();
			min = this.commandMinValues.getMinValue();
			
			this.commandMaxValues.setObjectiveIndex(i);
			this.commandMaxValues.execute();
			max = this.commandMaxValues.getMaxValue();

			// Set boundaries and width
			aux = ((max-min)/(2*div));
			lower = min - aux;
			upper = max + aux;
			width = (upper-lower)/div;

			this.spaceDivProperties.add(new GridObjectiveProperties(lower, upper, width));
		}
	}

	/**
	 * Adaptive grid assignment.
	 * This method assign the correspondent
	 * grid for each objective on each individual
	 * using the number of fixed divisions and
	 * the current limits in k-objective.
	 * 
	 * <p>lb_k = min_k(Population) - ( max_k(Population)-min_k(Population) / 2*divisions )</p>
	 * <p>ub_k = max_k(Population) - ( max_k(Population)-min_k(Population) / 2*divisions )</p>
	 * <p>hyperbox_widht_k = ( ub_k - lb_k ) / divisions </p>
	 * <p>grid_k(individual) = ( f_k(individual) - lb_k ) / hyperbox_width_k </p>
	 * 
	 * @param population Individuals within the population
	 * */
	protected void gridAssignment(List<IIndividual> population){

		double value;
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		int size = population.size();
		double bound, width;
		IIndividual ind;
		double fitnessValue;

		// Compute the grid for each individual and objective
		for(int i=0; i<size; i++){
			IFitness gridValues [] = new IFitness[nObj];
			ind = population.get(i);

			for(int j=0; j<nObj; j++){
				try {
					fitnessValue = ((MOFitness)ind.getFitness()).getObjectiveDoubleValue(j);

					bound = this.spaceDivProperties.get(j).getLowerBound();
					width = this.spaceDivProperties.get(j).getWidth();
					if(width!=0)
						value = (fitnessValue-bound)/width;
					else
						value = 0;	// only one grid
					if(isMaximized()){
						value = Math.ceil(value);
					}
					else{
						value = Math.floor(value);
					}
					gridValues[j] = new SimpleValueFitness(value);

				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			((IGrEAMOFitness)ind.getFitness()).setHypercube(new Hypercube(gridValues));
		}
	}

	/**
	 * The mating selection performs a tournament
	 * competition, between two candidate parents,
	 * generating a number of parents equal to the
	 * population size.
	 * Firstly, Pareto dominance and grid dominance
	 * are used to take the decision. If no dominance
	 * exists, it check the grid crowding distance.
	 * @param population Individuals to select from
	 * */
	protected ArrayList<IIndividual> tournamentSelection(List<IIndividual> population){

		ArrayList<IIndividual> parents = new ArrayList<IIndividual>();

		int indexP0, indexP1;
		IIndividual parent0, parent1;
		int paretoComparison, gridComparison;
		int size = population.size();
		double gcd0, gcd1;
		IRandGen randgen = getContext().createRandGen();
		MOFitnessComparator fcomparator = getSolutionComparator().getFitnessComparator();

		for(int i=0;i<size;i++){

			indexP0 = randgen.choose(0, size);
			indexP1 = randgen.choose(0, size);

			// Get individuals and its grid properties
			parent0 = population.get(indexP0);
			parent1 = population.get(indexP1);

			// Pareto comparison and grid comparison
			paretoComparison = fcomparator.compare(parent0.getFitness(), parent1.getFitness());
			gridComparison = this.hComparator.compare(parent0.getFitness(), parent1.getFitness());

			// There exists dominance between individuals
			if(paretoComparison!=0 || gridComparison!=0){
				if(paretoComparison==1 || gridComparison==1){	// First individual dominates
					parents.add(parent0);
				}
				else{	// Second individual dominates
					parents.add(parent1);
				}
			}

			// No dominance exists, check grid crowding distance
			else{
				gcd0 = ((IGrEAMOFitness)parent0.getFitness()).getCrowdingDistance();
				gcd1 = ((IGrEAMOFitness)parent1.getFitness()).getCrowdingDistance();

				if(gcd0 < gcd1){
					parents.add(parent0);
				}
				else if(gcd0 > gcd1){
					parents.add(parent1);
				}
				else if(randgen.coin()){
					parents.add(parent0);
				}
				else{
					parents.add(parent1);
				}
			}
		}
		return parents;
	}

	/**
	 * Compute the grid ranking
	 * for a given individual.
	 * @param grid The individual set of grids.
	 * @return Grid ranking value.
	 * */
	protected double computeGridRanking(Hypercube grid){
		IFitness values [] = grid.getValues();
		double gr = 0.0;
		for(int i=0; i<values.length; i++){
			gr += ((IValueFitness)values[i]).getValue();
		}
		return gr;
	}

	/**
	 * Compute the grid crowding distance
	 * for a given individual.
	 * @param grid The individual grid.
	 * @param population The current population.
	 * @return Grid crowding distance.
	 * */
	protected double computeGridCrowdingDistance(Hypercube grid, List<IIndividual> population){
		double density = 0.0;
		double gridDif;
		Hypercube other;
		int nObj = grid.getValues().length;
		int size = population.size();
		for(int i=0; i<size; i++){
			other = ((IGrEAMOFitness)population.get(i).getFitness()).getHypercube();
			gridDif = gridDifference(grid, other);
			// The other individual is considered as a neighbor
			if(gridDif < nObj){
				density += nObj-gridDif;
			}
		}
		return density;
	}

	/**
	 * Compute the grid difference between two individuals.
	 * @param grid0 First individual grid.
	 * @param grid1 Second individual grid.
	 * @return Grid difference.
	 * */
	protected double gridDifference(Hypercube grid0, Hypercube grid1){
		double dif = 0.0;
		IFitness values0 [] = grid0.getValues();
		IFitness values1 [] = grid1.getValues();
		int size = values0.length;
		double simpleValue0, simpleValue1;
		// Accumulates difference on each grid coordinate
		for(int i=0; i<size; i++){
			simpleValue0 = ((IValueFitness)values0[i]).getValue();
			simpleValue1 = ((IValueFitness)values1[i]).getValue();
			dif += Math.abs(simpleValue0-simpleValue1);
		}
		return dif;
	}

	/**
	 * Compute the grid point distance
	 * for a given individual to the
	 * utopian point in its hyperbox.
	 * @param ind The individual.
	 * @param grid The individual grid.
	 * @return Grid point distance.
	 * */
	protected double computeGridPointDistance(IIndividual ind, Hypercube grid){

		double pointDistance = 0.0;
		int nObj = this.spaceDivProperties.size();
		double utopianCoordinate, gridValue, width, fitnessValue, bound;
		MOFitness fitness = (MOFitness)ind.getFitness();
		Boolean isMaximized = isMaximized();

		for(int i=0; i<nObj; i++){

			if(isMaximized)
				bound = this.spaceDivProperties.get(i).getUpperBound();		// upper bound (better)
			else
				bound = this.spaceDivProperties.get(i).getLowerBound(); 	// lower bound (better)

			width = this.spaceDivProperties.get(i).getWidth();			// grid width
			gridValue = ((IValueFitness)grid.getValue(i)).getValue();	// grid position

			// objective value
			try {
				fitnessValue = fitness.getObjectiveDoubleValue(i);

				if(width!=0)
					utopianCoordinate = (fitnessValue - (bound + gridValue*width))/width;
				// There not exist any grid, all coordinates are the same that the bound
				else
					utopianCoordinate = 0.0;	
				pointDistance += utopianCoordinate*utopianCoordinate;

			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			} 
		}
		pointDistance = Math.sqrt(pointDistance);

		return pointDistance;
	}

	/**
	 * Initialize grid metrics for the environmental
	 * selection.
	 * @param front Individuals in the critical front.
	 * */
	protected void initializeGridFront(List<IIndividual> front){
		double gridRanking;
		double gridPointDistance;
		Hypercube grid;
		int size = front.size();
		IIndividual ind;

		for(int i=0; i<size; i++){
			ind = front.get(i);
			grid = ((IGrEAMOFitness)ind.getFitness()).getHypercube();

			// Compute grid ranking
			gridRanking = computeGridRanking(grid);
			((IGrEAMOFitness)ind.getFitness()).setRanking(gridRanking);

			// Compute grid point distance
			gridPointDistance = computeGridPointDistance(ind, grid);
			((IGrEAMOFitness)ind.getFitness()).setCoordinatePointDistance(gridPointDistance);

			// Grid crowding distance is equal to zero at this point
			((IGrEAMOFitness)ind.getFitness()).setCrowdingDistance(0.0);
		}
	}

	/**
	 * Find the best individual from a given
	 * set of individuals using the grid metrics.
	 * @param front Individuals in the critical front.
	 * @return Best individual in the front.
	 * */
	protected IIndividual findBest(List<IIndividual> front){

		IIndividual other;
		double bestRanking, bestCrowding, bestDistance, otherRanking, otherCrowding, otherDistance;
		boolean update = false;
		boolean maximize = isMaximized();

		IIndividual best = front.get(0);

		bestRanking = ((IGrEAMOFitness)best.getFitness()).getRanking();
		bestCrowding = ((IGrEAMOFitness)best.getFitness()).getCrowdingDistance();
		bestDistance = ((IGrEAMOFitness)best.getFitness()).getCoordinatePointDistance();

		for(int i=1; i<front.size(); i++){

			// Get grid metrics in the individual to compare with
			other = front.get(i);

			otherRanking = ((IGrEAMOFitness)other.getFitness()).getRanking();
			otherCrowding = ((IGrEAMOFitness)other.getFitness()).getCrowdingDistance();
			otherDistance = ((IGrEAMOFitness)other.getFitness()).getCoordinatePointDistance();

			// Best grid ranking
			if((maximize && otherRanking > bestRanking) || !maximize && otherRanking < bestRanking){
				update = true;
			}
			// Equal grid ranking, check crowding distance
			else if(otherRanking == bestRanking){
				// Better crowding distance
				if(otherCrowding < bestCrowding){
					update = true;
				}
				// Equal grid ranking and crowding distance, check point distance
				else if(otherCrowding == bestCrowding){
					if(otherDistance < bestDistance){
						update = true;
					}
				}
			}

			// The individual is better than the current best				
			if(update){
				best = front.get(i);
				bestRanking = ((IGrEAMOFitness)best.getFitness()).getRanking();
				bestCrowding = ((IGrEAMOFitness)best.getFitness()).getCrowdingDistance();
				bestDistance = ((IGrEAMOFitness)best.getFitness()).getCoordinatePointDistance();
				update=false;
			}
		}
		return best;
	}

	/**
	 * It performs and adjustment in the crowding
	 * distance of a set of individuals (front)
	 * considering its proximity to the picked
	 * individual.
	 * @param front Set of individual in the critical front.
	 * @param individual Picked individual (the best in the front).
	 * */
	protected void gridCrowdingDistanceAdjustment(List<IIndividual> front, IIndividual individual){
		double dif, crowdingDistance;
		Hypercube indGrid = ((IGrEAMOFitness)individual.getFitness()).getHypercube();
		Hypercube otherGrid;
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		int size = front.size();

		for(int i=0; i<size; i++){
			otherGrid = ((IGrEAMOFitness)front.get(i).getFitness()).getHypercube();
			dif = gridDifference(indGrid, otherGrid);
			if(dif < nObj){
				crowdingDistance = ((IGrEAMOFitness)individual.getFitness()).getCrowdingDistance() + (nObj - dif);
				((IGrEAMOFitness)front.get(i).getFitness()).setCrowdingDistance(crowdingDistance);
			}
		}
	}

	/**
	 * It performs an adjustment in the grid ranking of
	 * a set of individuals (front) considering the proximity
	 * and the grid dominance relation to the picked individual.
	 * @param front The individuals in the critical front.
	 * @param individual The picked individual.
	 * */
	protected void gridRankingAdjustment(List<IIndividual> front, IIndividual individual){

		// Classify individuals in collections using the three criteria
		List<IIndividual> equalDistance = new ArrayList<IIndividual>();
		List<IIndividual> dominated = new ArrayList<IIndividual>();
		List<IIndividual> nonDominated = new ArrayList<IIndividual>();
		List<IIndividual> neighbors = new ArrayList<IIndividual>();

		IGrEAMOFitness otherProperties, other2Properties;
		Hypercube bestGrid = ((IGrEAMOFitness)individual.getFitness()).getHypercube();
		Hypercube otherGrid;
		double dif;
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		int size = front.size();
		IIndividual other;

		// Create the collections of individuals
		for(int i=0; i<size; i++){
			other = front.get(i);
			otherProperties = (IGrEAMOFitness)other.getFitness();
			otherGrid = otherProperties.getHypercube();

			// Check equality in the grid distance
			dif = gridDifference(bestGrid, otherGrid);
			if(dif == 0){
				equalDistance.add(other);
			}

			// Check grid distance
			else if(dif<nObj){
				neighbors.add(other);
			}

			// Check if the best dominates (grid comparison) the other individual
			if(this.hComparator.compare(individual.getFitness(), other.getFitness())==1){
				dominated.add(other);
			}
			else{
				nonDominated.add(other);
			}
		}

		// Adjust grid ranking in each collection

		// Individuals with equal distance
		size = equalDistance.size();
		for(int i=0; i<size; i++){
			otherProperties = (IGrEAMOFitness)equalDistance.get(i).getFitness();
			otherProperties.setRanking(otherProperties.getRanking()+nObj+2);
		}

		// Individuals which are dominated
		size = dominated.size();
		for(int i=0; i<size; i++){
			otherProperties = (IGrEAMOFitness)dominated.get(i).getFitness();
			otherProperties.setRanking(otherProperties.getRanking()+nObj);
		}

		// Initialize penalty degree
		ArrayList<IIndividual> auxSet = new ArrayList<IIndividual>();
		size = nonDominated.size();
		for(int i=0; i<size; i++){
			other = nonDominated.get(i);
			if(!equalDistance.contains(other)){
				auxSet.add(other);
				((IGrEAMOFitness)other.getFitness()).setPenaltyDegree(0.0);
			}
		}

		// Neighbors
		size = neighbors.size();
		int size2 = front.size();
		IIndividual other2;
		for(int i=0; i<size; i++){
			other = neighbors.get(i);
			if(nonDominated.contains(other) && !equalDistance.contains(other)){
				otherProperties = (IGrEAMOFitness)other.getFitness();
				otherGrid = otherProperties.getHypercube();
				dif = gridDifference(bestGrid, otherGrid);

				if(otherProperties.getPenaltyDegree() < (nObj - dif)){
					otherProperties.setPenaltyDegree(nObj - dif);

					for(int j=0; j<size2; j++){
						other2 = front.get(j);
						other2Properties = (IGrEAMOFitness)other2.getFitness();
						if(this.hComparator.compare(other.getFitness(), other2.getFitness()) == 1
								&& (!dominated.contains(other2) 
										|| !equalDistance.contains(other2))
										&& other2Properties.getPenaltyDegree() < otherProperties.getPenaltyDegree()){

							other2Properties.setPenaltyDegree(otherProperties.getPenaltyDegree());
						}
					}
				}
			}
		}

		// Set new ranking on non-dominated individuals with
		// different grid ranking
		double ranking;
		size = auxSet.size();
		for(int i=0; i<size; i++){
			otherProperties = (IGrEAMOFitness)auxSet.get(i).getFitness();
			ranking = otherProperties.getRanking();
			otherProperties.setRanking(ranking+ otherProperties.getPenaltyDegree());
		}
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create the comparator of hypercubes
	 * using the information of objectives.
	 * */
	private void createHypercubeComparator(){
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int nObj = objectives.size();
		Comparator<IFitness> [] componentComparators = new ValueFitnessComparator[nObj];
		Objective obj;
		for(int i=0; i<nObj; i++){
			obj = objectives.get(i);
			componentComparators[i] = new ValueFitnessComparator(!obj.isMaximized());
		}
		this.hComparator = new EpsilonDominanceComparator(componentComparators);
	}
}
