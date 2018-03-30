package services;

import javax.inject.Inject;

import akka.actor.ActorSystem;
import play.api.libs.concurrent.CustomExecutionContext;

/**
 * @author Sumit Srivastava
 *
 */
public class DatabaseExecutionContext extends CustomExecutionContext {

	@Inject
	public DatabaseExecutionContext(ActorSystem system) {
		super(system, "play.db");
	}
	
}
