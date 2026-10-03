TARGETS: getCovariances(problem) dimension handling for bound params
ORACLES: cov matrix size must equal #free (unbound) parameters, not total parameters
CASES: problem with 0 bound,some bound,all bound params; also0 measurements
RISKS: abstract class needs concrete subclass; must set bound via EstimatedParameter.setBound(true)