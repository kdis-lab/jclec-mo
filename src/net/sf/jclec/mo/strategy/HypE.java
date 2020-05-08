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
import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.MaxObjectiveValue;
import net.sf.jclec.mo.command.MinObjectiveValue;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.command.PopulationSplitter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * HypE strategy
 * 
 * <p>The <b>Hyp</b>ervolume <b>E</b>stimation Algorithm for
 * Multiobjective Optimization proposes the use of the
 * hypervolume indicator to guide the search toward the
 * Pareto front. The fitness assignment method, used in
 * both the mating and the environmental selection, is 
 * based on the estimation of this indicator. More
 * specifically, it considers the contribution of individuals
 * to the indicator without requiring its exact calculation.
 * A Monte Carlo simulation is used to sample the population
 * in the fitness calculation, allowing a fast procedure when
 * the number of objectives is high.</p>
 * 
 * <p>This implementation supports both minimization and
 * maximization problems and also different scales in the
 * objective functions. Objective values are scaled before
 * computing the hypervolume contribution. The unique precondition
 * is that all objectives should be maximized or minimized, combining
 * both types of objectives is not allowed.</p>
 * 
 * <p><i>Paper</i>: J. Bader, E. Ziztler, “HypE: An Algorithm for Fast 
 * Hypervolume-Based Many Objective Optimization”, Evolutionary Computation, 
 * vol. 19, no. 1, pp. 45-76, 2011.</p>
 * 
 * <p><i>Adapted from the original implementation</i>: 
 * http://www.tik.ee.ethz.ch/sop/pisa/selectors/hype/?page=hype.php</p>
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
public class HypE extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1387128577341836392L;

	/** Number of sample points */
	private int samplingSize;

	/** Minimum value for each objective in the current population */
	protected double [] minBounds;

	/** Maximum value for each objective in the current population */
	protected double [] maxBounds;

	/** Objective values are in the range [0,1]*/
	protected boolean isScaled;

	/** Command to split population into fronts */
	protected PopulationSplitter commandSplit;

	/** Command to calculate minimum objective values */
	protected MinObjectiveValue commandMinValues;

	/** Command to calculate maximum objective values */
	protected MaxObjectiveValue commandMaxValues;

	/** Command to invert objective values */
	protected ObjectiveInverter commandInvert;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public HypE(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the sampling size.
	 * @param samplingSize New sampling size.
	 * */
	protected void setSamplingSize(int samplingSize){
		this.samplingSize = samplingSize;
	}

	/**
	 * Get the sampling size.
	 * @return Current sampling size.
	 * */
	public int getSamplingSize(){
		return this.samplingSize;
	}

	/**
	 * Set the scale flag.
	 * @param isScaled New flag value.
	 * */
	protected void setScaleFlag(boolean isScaled){
		this.isScaled = isScaled;
	}

	/**
	 * Get the scale flag.
	 * @return Current scale flag value.
	 * */
	public boolean isScaled(){
		return this.isScaled;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for HypE are:
	 * <ul>
	 * 	<li>sampling-size (<code>integer</code>): <p>Number of samples 
	 * for hypervolume estimation.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);

		// Sampling size
		int samplingSize = settings.getInt("sampling-size", 10000);
		setSamplingSize(samplingSize);

		// Check objectives
		if(isMaximized() == null){
			System.err.println("All objectives should be minimized or maximized");
			System.exit(-1);
		}

		// Check objective limits and set the scale flag
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		boolean isInRange = true;
		Objective obj;
		int size = objectives.size();
		for(int i=0; isInRange && i<size; i++){
			obj = objectives.get(i);
			if(obj.getMinimum() != 0 || obj.getMaximum() != 1){
				isInRange = false;
			}
		}
		setScaleFlag(isInRange);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Initialize the auxiliary commands
		this.commandSplit = new PopulationSplitter();
		this.commandSplit.setComparator(this.comparator);
		this.commandMaxValues = new MaxObjectiveValue();
		this.commandMinValues = new MinObjectiveValue();
		this.commandInvert = new ObjectiveInverter();

		// Initialize objective bounds
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		this.minBounds = new double [nObjectives];
		this.maxBounds = new double [nObjectives];
		
		// HypE does not use an archive
		return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>HypE uses a Pareto comparator.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>HypE performs a binary tournament competition
	 * based on the fitness of each individual (hypervolume
	 * contribution).</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {

		List<IIndividual> parents = new ArrayList<IIndividual>();

		// Selection based on hypervolume fitness values
		fitnessAssignment(population, null);

		int size = population.size();
		IIndividual parent0, parent1;
		int rndIndex0, rndIndex1;
		double fitness0, fitness1;
		IRandGen randgen = getContext().createRandGen();

		// Select parents
		for(int i=0; i<size; i++){

			// Choose one individual in the current population
			rndIndex0 = randgen.choose(size);
			parent0 = population.get(rndIndex0);

			// Choose another individual
			do{
				rndIndex1 = getContext().createRandGen().choose(size);
			}while(rndIndex0==rndIndex1);
			parent1 = population.get(rndIndex1);

			// Binary tournament
			fitness0 = ((MOFitness)parent0.getFitness()).getValue();
			fitness1 = ((MOFitness)parent1.getFitness()).getValue();

			// Add the winner of the tournament as parent
			if(fitness0>fitness1){
				parents.add(parent0);
			}
			else if(fitness1>fitness0){
				parents.add(parent1);
			}
			// If a tie occurs, choose one individual randomly 
			else{
				if(randgen.coin())
					parents.add(parent0);
				else
					parents.add(parent1);
			}
		}
		return parents;
	}


	/**
	 * {@inheritDoc}
	 * <p>HypE selects the non dominated solutions to
	 * conform the new population. After ordering the
	 * population by fronts, it iteratively fill the
	 * new population with the best fronts. The selection
	 * in the critical front is based on the minimum loss
	 * of hypervolume, i.e. individuals with less contribution
	 * to the overall hypervolume are discarded.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join populations
		List<IIndividual> allInds = new ArrayList<IIndividual>();
		allInds.addAll(population);
		for(IIndividual ind: offspring)
			if(!allInds.contains(ind))
				allInds.add(ind.copy());

		// Firstly, split the population into fronts
		this.commandSplit.setPopulation(population);
		this.commandSplit.execute();
		List<List<IIndividual>> populationIntoFronts = this.commandSplit.getSplitPopulation();

		// Copy non dominated solutions by fronts
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		int size =  population.size();
		int index = 0;
		int maxFronts = populationIntoFronts.size();

		// The new population is created with the individuals of the best fronts
		while(index<maxFronts && (survivors.size() + populationIntoFronts.get(index).size()) <= size ){
			survivors.addAll(populationIntoFronts.get(index));
			index++;
		}

		// Select the remaining individuals front the critical front
		int fill = size-survivors.size();
		List<IIndividual> selected = null;
		if(fill>0){
			selected = frontReduction(populationIntoFronts.get(index), fill);
			for(IIndividual ind: selected)
				survivors.add(ind.copy());
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>HypE does not use an archive of solutions.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		// Do nothing
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>HypE assigns a fitness value based on
	 * the hypervolume contribution of each
	 * individual.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {

		int size = population.size();
		Vector<Double> values;
		Vector<Double> points;
		int indexes [] = new int[size];

		for(int i=0; i<size; i++){
			indexes[i] = i;
		}

		// Extract and properly order the objective values
		points = getObjectiveValues(indexes, population);

		// Compute the indicator
		values = hypeIndicator(points, size, 0, 1, getSamplingSize(), size);

		// Assign the fitness
		for(int i=0; i<size; i++){
			((MOFitness)population.get(i).getFitness()).setValue(values.get(i));
		}
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Stores the objective values of the individuals
	 * that are located at the specified positions.
	 * @param indexes Indexes of individuals to be considered.
	 * @param population The list of individuals.
	 * */
	protected Vector<Double> getObjectiveValues(int [] indexes, List<IIndividual> population){

		int size = population.size();
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().size();
		Vector<Double> points = new Vector<Double>(size*nObjectives);

		// Invert values if the problem should be maximized
		if(isMaximized()){
			this.commandInvert.setPopulation(population);
			this.commandInvert.execute();
		}

		// Compute bounds in the current population
		computeBounds(population);

		// Copy the values of the objectives in the proper way
		// the objective values are scaled
		double value;
		for(int i=0; i<size; i++){
			for(int k=0; k<nObjectives; k++){
				try {
					value = ((MOFitness)population.get(indexes[i]).getFitness()).getObjectiveDoubleValue(k);
					if(this.maxBounds[k]!=this.minBounds[k])
						value = (value-this.minBounds[k])/(this.maxBounds[k]-this.minBounds[k]);
					else
						value = 1.0;
					points.add(value);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		// Undo the objective inversion
		if(isMaximized()){
			this.commandInvert.setPopulation(population);
			this.commandInvert.execute();
		}

		return points;
	}

	/**
	 * Compute the hypervolume contribution for a given set
	 * of points in the search space.
	 * @param points Objective values in the search space
	 * @param size Number of points (individuals).
	 * @param lowerbound Lower value in the sampling.
	 * @param upperbound Greater value in the sampling.
	 * @param samplingsize Number of samples, -1 for exact calculation.
	 * @param k Variable k for hypervolume calculation.
	 * @return The hypervolume contribution estimation for each individual.
	 * */
	protected Vector<Double> hypeIndicator(Vector<Double> points, int size, 
			double lowerbound, double upperbound, int samplingsize, int k){

		double rho [] = new double[k+1];
		rho[0] = 0;
		for(int i=1; i<k; i++){
			rho[i] = 1.0/i;
			for(int j=1; j<=i-1; j++){
				rho[i] *= ((double)k-j)/((double)size - j);
			}
		}

		if(this.samplingSize < 0){
			return exactHypervolumeValues(points, rho, lowerbound, upperbound, k, size);
		}
		else
			return samplingHypervolumeValues(points, rho, lowerbound, upperbound, k, size, this.samplingSize);
	}

	/**
	 * Reduce the size of the critical front using
	 * the hypervolume indicator.
	 * @param individualsFront Individuals in the critical front.
	 * @param nSelect Number of individuals to be selected.
	 * */
	protected List<IIndividual> frontReduction(List<IIndividual> individualsFront, int nSelect){

		List<IIndividual> result = new ArrayList<IIndividual>();

		// Copy current front (more individuals that the needed)
		for(IIndividual ind: individualsFront){
			result.add(ind.copy());
		}

		// Compute indicator and remove worst individuals
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		Vector<Double> values;
		Vector<Double> points, auxpoints;
		double value, minValue = Double.MAX_VALUE;
		int select = -1;
		int n = individualsFront.size();

		int indexes [] = new int[n];
		for(int i=0; i<n; i++)
			indexes[i] = i;
		points = getObjectiveValues(indexes, result);

		// Progressively remove the worst individuals in the set
		// until it has the desired size
		while(result.size() > nSelect){

			values = hypeIndicator(points, result.size(), 0, 1, getSamplingSize(), n);
			select = -1;
			minValue = Double.MAX_VALUE;

			// Search the worst indicator value
			for(int i=0; i<result.size(); i++){
				value = values.get(i);
				if(value<minValue){
					minValue = value;
					select = i;
				}
			}

			// Remove the individual with lower indicator value
			result.remove(select);
			values.remove(select);

			// Remove its points
			int init = select*nObjectives;
			int end = init + nObjectives;

			for(int i=init; i<end; i++){
				points.set(i,-1.0);
			}
			auxpoints = new Vector<Double>();
			for(int i=0; i<points.size(); i++){
				if(points.get(i)!=-1){
					auxpoints.add(points.get(i));
				}
			}
			points = auxpoints;

			// Decrement the size of the front
			n--; 
		}

		return result;
	}

	/**
	 * Compute the exact hypervolume value
	 * for each individual.
	 * @param points The set of objectives values of all the individuals.
	 * @param rho The weight of each hypervolume partition (contribution).
	 * @param lowerbound Lower bound in the objective space.
	 * @param upperbound Upper bound in the objective space.
	 * @param kPartitions Number of hypervolume partitions to be considered.
	 * @return Fitness values for each individual (in the order established by points).
	 * */
	protected Vector<Double> exactHypervolumeValues(Vector<Double> points, double [] rho, 
			double lowerbound, double upperbound, int kPartitions, int size){

		Vector<Double> values = new Vector<Double>(size);
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double [] bounds = new double[nObjectives];
		Vector<Integer> indexes = new Vector<Integer>(size);

		for(int i=0; i<nObjectives; i++) // Reference point is set to 1 for all objectives
			bounds[i] = 1;
		for(int i=0; i<size; i++)
			indexes.add(i);

		// Compute hypervolume
		hypervolumeRecursive(points, size, nObjectives, size, nObjectives-1, bounds, indexes, values, rho, kPartitions);

		return values;
	}

	/**
	 * Compute the estimated hypervolume value
	 * for each individual.
	 * @param points The set of objectives values of all the individuals.
	 * @param rho The weight of each hypervolume partition (contribution).
	 * @param lowerbound Lower bound in the objective space.
	 * @param upperbound Upper bound in the objective space.
	 * @param param_k Number of hypervolume partitions to be considered.
	 * @return Fitness values for each individual (in the order established by points).
	 * */
	protected Vector<Double> samplingHypervolumeValues(Vector<Double> points, double [] rho, double lowerbound, 
			double upperbound, int kPartitions, int popSize, int samplingSize){

		Vector<Double> values = new Vector<Double>(popSize);
		for(int i=0; i<popSize; i++)
			values.add(0.0);

		int [] hitstat = new int[popSize];
		int domCount;
		int dim = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		IRandGen randgen = getContext().createRandGen();
		double [] sample = new double[dim];
		double [] point = new double[dim];

		for(int s=0; s<samplingSize; s++) {
			for(int k=0; k < dim; k++ ){
				double j = randgen.uniform(lowerbound, upperbound);
				sample[k]=j;
			}

			domCount = 0;
			for(int i = 0; i < popSize; i++ ) {
				for(int j=0; j<dim; j++){
					point[j] = points.get(i*dim+j);
				}
				if( weaklyDominates(point, sample, dim) ) {
					domCount++;
					if( domCount > kPartitions )
						break;
					hitstat[i] = 1;
				}
				else
					hitstat[i] = 0;
			}
			if( domCount > 0 && domCount <= kPartitions ){
				for(int i = 0; i < popSize; i++ ){
					if( hitstat[i] == 1 )
						values.set(i, values.get(i)+rho[domCount]);
				}
			}
		}
		for(int i=0; i<popSize; i++){
			values.set(i, values.get(i) * Math.pow( (upperbound-lowerbound), dim ) / samplingSize);
		}

		return values;
	}

	/**
	 * Recursively compute the hypervolume
	 * contribution on each dimension.
	 * @param input_p The set of objectives values of all the individuals.
	 * @param pnts The number of points in input_p.
	 * @param dim The number of objectives (the dimension).
	 * @param nrOfPnts The number of points in input_pvec.
	 * @param actDim The current dimension.
	 * @param bounds The reference set.
	 * @param input_pvec The set of points for overlapping.
	 * @param fitness Fitness values being computed.
	 * @param rho The weight of each hypervolume partition (contribution).
	 * @param param_k Number of hypervolume partitions to be considered.
	 * */
	protected void hypervolumeRecursive(Vector<Double> input_p, int pnts, int dim, int nrOfPnts, int actDim, 
			double [] bounds, Vector<Integer> input_pvec, Vector<Double> fitness, double [] rho, int param_k){

		double extrusion;
		Vector<Integer> pvec = new Vector<Integer>(pnts);
		Vector<Double> p = new Vector<Double>(pnts*dim);

		for(int i=0; i<pnts; i++) {
			fitness.add(0.0);
			pvec.add(input_pvec.get(i));
		}
		for(int i=0; i<pnts*dim; i++)
			p.add(input_p.get(i));

		rearrangeIndicesByColumn(p, nrOfPnts, dim, actDim, pvec);

		for(int i = 0; i < nrOfPnts; i++ ) {
			if( i < nrOfPnts - 1 )
				extrusion = p.get((pvec.get(i+1))*dim + actDim) - p.get(pvec.get(i)*dim + actDim);
			else
				extrusion = bounds[actDim] - p.get(pvec.get(i)*dim + actDim);

			if( actDim == 0 ) {
				if( i+1 <= param_k )
					for(int j=0; j<=i; j++) {
						fitness.set(pvec.get(j), fitness.get(pvec.get(j)) + extrusion*rho[i+1]);
					}
			}
			else if( extrusion > 0 ) {
				Vector<Double> tmpfit = new Vector<Double>(pnts);
				hypervolumeRecursive(p, pnts, dim, i+1, actDim-1, bounds, pvec, tmpfit, rho, param_k );
				for(int j=0; j<pnts; j++)
					fitness.set(j, fitness.get(j) + extrusion*tmpfit.get(j));
			}
		}
	}

	/**
	 * Auxiliary function for hypervolumeRecursive
	 * */
	protected void rearrangeIndicesByColumn(Vector<Double> mat, int rows, int columns, int col, Vector<Integer> ind){
		int maxLevels = 300;
		int [] beg = new int[maxLevels];
		int [] end = new int[maxLevels]; 
		int i = 0, L, R, swap;
		double pref;
		int pind;
		double [] ref = new double[rows];

		for( i = 0; i < rows; i++ ) {
			ref[i] = mat.get(col + ind.get(i)*columns);
		}
		i = 0;

		beg[0] = 0; end[0] = rows;
		while ( i >= 0 ) {
			L = beg[i]; R = end[i]-1;
			if( L < R ) {
				pref = ref[ L ];
				pind = ind.get(L);
				while( L < R ) {
					while( ref[ R ] >= pref && L < R )
						R--;
					if( L < R ) {
						ref[ L ] = ref[ R ];
						ind.set(L++, ind.get(R));
					}
					while( ref[L] <= pref && L < R )
						L++;
					if( L < R) {
						ref[ R ] = ref[ L ];
						ind.set(R--, ind.get(L));
					}
				}
				ref[ L ] = pref; ind.set(L,pind);
				beg[i+1] = L+1; end[i+1] = end[i];
				end[i++] = L;
				if( end[i] - beg[i] > end[i-1] - beg[i-1] ) {
					swap = beg[i]; beg[i] = beg[i-1]; beg[i-1] = swap;
					swap = end[i]; end[i] = end[i-1]; end[i-1] = swap;
				}
			}
			else {
				i--;
			}
		}
	}

	/**
	 * Compare if the first point 'weakly dominates' another
	 * individual. As it is used for the hypervolume estimation,
	 * a minimization problem is considered.
	 * @param point1 First point.
	 * @param point2 Second point.
	 * @return True if the first point 'weakly dominates' the
	 * second point, false otherwise.
	 * */
	protected boolean weaklyDominates(double [] point1, double [] point2, int nObjectives){
		boolean better = true;
		int i=0;
		while(i<nObjectives && better){
			better = point1[i]<=point2[i];
			i++;
		}
		return better;
	}

	/**
	 * Compute the minimum and maximum
	 * value for each objective in the
	 * current population.
	 * @param population The current population.
	 * */
	protected void computeBounds(List<IIndividual> population){
		
		// Minimum and maximum value for each objective in the current population
		int size = this.maxBounds.length;
		this.commandMinValues.setPopulation(population);
		this.commandMaxValues.setPopulation(population);

		for(int i=0; i<size; i++){

			// Minimum value
			this.commandMinValues.setObjectiveIndex(i);
			this.commandMinValues.execute();
			this.minBounds[i] = this.commandMinValues.getMinValue();

			// Maximum value
			this.commandMaxValues.setObjectiveIndex(i);
			this.commandMaxValues.execute();
			this.maxBounds[i] = this.commandMaxValues.getMaxValue();
		}
	}
}
