package com.almende.eve.context.google;

import java.io.IOException;

import com.almende.eve.context.Context;
import com.almende.eve.scheduler.Scheduler;
import com.almende.eve.scheduler.google.AppEngineScheduler;
import com.almende.eve.config.Config;
import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;

public class DatastoreContext implements Context {
	private DatastoreContextFactory factory = null;
	private Scheduler scheduler = null;
	private String agentClass = null;
	private String agentId = null;
	private String agentUrl = null;
	
	public DatastoreContext() {}

	protected DatastoreContext(DatastoreContextFactory factory, 
			String agentClass, String agentId) {
		this.factory = factory;
		this.agentClass = agentClass;
		this.agentId = agentId;
		// Note: agentUrl will be initialized when needed
	}

	/**
	 * Retrieve the url of the agents app from the system environment
	 * eve.properties, for example "http://myapp.appspot.com"
	 * 
	 * @return appUrl
	 */
	/* TODO: cleanup
	// TODO: replace this with usage of environment
	private String getAppUrl() {
		String appUrl = null;
	
		// TODO: retrieve the servlet path from the servlet parameters itself
		// http://www.jguru.com/faq/view.jsp?EID=14839
		// search for "get servlet path without request"
		// System.out.println(req.getServletPath());

		String environment = SystemProperty.environment.get();
		String id = SystemProperty.applicationId.get();
		// String version = SystemProperty.applicationVersion.get();
		
		if (environment.equals("Development")) {
			// TODO: check the port
			appUrl = "http://localhost:8888";
		} else {
			// production
			// TODO: reckon with the version of the application?
			appUrl = "http://" + id + ".appspot.com";
			// TODO: use https by default
			//appUrl = "https://" + id + ".appspot.com";
		}
		
		return appUrl;
	}
	*/
	
	@Override
	public String getAgentId() {
		return agentId;
	}

	@Override
	public String getAgentClass() {
		return agentClass;
	}
	
	/**
	 * Generate the full key, which is defined as "id.key"
	 * @param key
	 * @return
	 */
	private String getFullKey (String key) {
		return agentId + "." + key;
	}
	
	// TODO: load and save in a transaction
	
	@Override
	public <T> T get(String key, Class<T> type) {
		ObjectDatastore datastore = new AnnotationObjectDatastore();
		
		String fullKey = getFullKey(key);
		KeyValue entity = datastore.load(KeyValue.class, fullKey);
		if (entity != null) {
			try {
				return entity.getValue(type);
			} catch (ClassNotFoundException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public boolean put(String key, Object value) {
		ObjectDatastore datastore = new AnnotationObjectDatastore();

		try {
			String fullKey = getFullKey(key);
			KeyValue entity = new KeyValue(fullKey, value);
			datastore.store(entity);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
	}

	@Override
	public boolean has(String key) {
		ObjectDatastore datastore = new AnnotationObjectDatastore();

		String fullKey = getFullKey(key);
		KeyValue entity = datastore.load(KeyValue.class, fullKey);
		return (entity != null);
	}

	@Override
	public void remove(String key) {
		ObjectDatastore datastore = new AnnotationObjectDatastore();

		String fullKey = getFullKey(key);
		KeyValue entity = datastore.load(KeyValue.class, fullKey);
		if (entity != null) {
			datastore.delete(entity);
		}
	}

	@Override
	public String getAgentUrl() {
		if (agentUrl == null) {
			String servletUrl = getServletUrl();
			if (servletUrl != null) {
				agentUrl = servletUrl;
				if (!agentUrl.endsWith("/")) {
					agentUrl += "/";
				}
				if (agentClass != null) {
					agentUrl += agentClass + "/";
					if (agentId != null) {
						agentUrl += agentId + "/";
					}
				}
			}			
		}
		return agentUrl;
	}
	
	@Override
	public String getServletUrl() {
		return factory.getServletUrl();
	}	
	
	@Override 
	public String getEnvironment() {
		return factory.getEnvironment();
	}
	
	@Override
	public Config getConfig() {
		return factory.getConfig();
	}

	@Override
	public Scheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new AppEngineScheduler();
		}
		return scheduler;
	}
}