import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {
	
	public void doJob() {
		
		// check if database is empty 
		if (User.count() == 0)
			Fixtures.load("initial-data.yml");
	}
}
