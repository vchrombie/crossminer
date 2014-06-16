module FileChurn

import org::ossmeter::metricprovider::Manager;
import org::ossmeter::metricprovider::ProjectDelta;

@metric{churnPerFile}
@doc{Count churn}
@friendlyName{Counts number of lines added and deleted per file}
@appliesTo{generic()}
map[loc file, int churn] churnPerFile(ProjectDelta delta = \empty())
  = (co : churn(co) | /VcsCommitItem co := delta)
  ;
  
int churn(VcsCommitItem item) 
  = (0 | it + count | /linesAdded(count) := item)
  + (0 | it + count | /linesDeleted(count) := item)
  ;
  
