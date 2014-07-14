package org.sigaim.siie.ws;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.sigaim.siie.dadl.DADLManager;
import org.sigaim.siie.dadl.OpenEHRDADLManager;
import org.sigaim.siie.db.PersistenceManager;
import org.sigaim.siie.db.sql.SQLPersistenceManager;
import org.sigaim.siie.interfaces.eql.IntSIIE001EQL;
import org.sigaim.siie.interfaces.eql.sigaim.SigaimIntSIIE001EQL;
import org.sigaim.siie.interfaces.reportmanagement.IntSIIE004ReportManagement;
import org.sigaim.siie.interfaces.reportmanagement.sigaim.SigaimIntSIIE004ReportManagement;
import org.sigaim.siie.interfaces.saprm.DelegatingSigaimIntSIIE004SAPRM;
import org.sigaim.siie.interfaces.saprm.DummyINT004SIIESAPRMProxy;
import org.sigaim.siie.interfaces.saprm.INT004SIIESAPRMProxy;
import org.sigaim.siie.rm.ReferenceModelManager;
import org.sigaim.siie.rm.ReflectorReferenceModelManager;
import org.sigaim.siie.seql.engine.SEQLEngine;
import org.sigaim.siie.seql.engine.SEQLPipeEngine;
import org.sigaim.siie.seql.engine.execution.SEQLExecutionMemorySolverStage;
import org.sigaim.siie.seql.engine.preprocessing.SEQLPreprocessingValidateIdentifiedVariablesStage;

public class ServiceInjector {
	private static final ServiceInjector instance = new ServiceInjector();
	private DADLManager dadlManager;
	private PersistenceManager persistenceManager;
	private ReferenceModelManager referenceModelManager;
	private INT004SIIESAPRMProxy saprm;
	private SEQLEngine seqlEngine;
	private IntSIIE001EQL intSIIE001EQL;
	private IntSIIE004ReportManagement intSIIE004ReportManagement;
	private DataSource dbDataSource;
	
	private ServiceInjector(){
		try {
			//Get the database resource
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource)envCtx.lookup("jdbc/SIGAIMSIIE");
			Boolean useSaprm = (Boolean) envCtx.lookup("conf/USESAPRM");

			System.out.println("Using real saprm: "+useSaprm);
			if(ds==null) {
				throw new IllegalArgumentException("Invalid database datasource");
			} else {
				dbDataSource=ds;
			}
			this.dadlManager=new OpenEHRDADLManager();
			this.referenceModelManager=new ReflectorReferenceModelManager(this.dadlManager);
			SQLPersistenceManager sqlManager=new SQLPersistenceManager(ds);
			sqlManager.setDADLManager(this.dadlManager);
			sqlManager.setReferenceModelManager(this.referenceModelManager);
			this.persistenceManager=sqlManager;
			if(useSaprm) {
				this.saprm=new DelegatingSigaimIntSIIE004SAPRM();
			} else {
				this.saprm=new DummyINT004SIIESAPRMProxy();				
			}
			SEQLExecutionMemorySolverStage stage=new SEQLExecutionMemorySolverStage(persistenceManager,referenceModelManager,dadlManager);
			SEQLPipeEngine engine=new SEQLPipeEngine();
			engine.addPreprocessStage(new SEQLPreprocessingValidateIdentifiedVariablesStage());
			engine.addExecutionStage(stage);
			this.seqlEngine=engine;
			this.intSIIE001EQL=new SigaimIntSIIE001EQL(engine,dadlManager);
			this.intSIIE004ReportManagement=new SigaimIntSIIE004ReportManagement(persistenceManager,referenceModelManager,dadlManager,saprm,engine);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public IntSIIE001EQL getIntSIIE001EQL() {
		return intSIIE001EQL;
	}
	public IntSIIE004ReportManagement getIntSIIE004ReportManagement() {
		return intSIIE004ReportManagement;
	}
	public static ServiceInjector getInstance() {
		return instance;
	}
	public DADLManager getDADLManager() {
		return this.dadlManager;
	}
	public ReferenceModelManager getReferenceModelManager() {
		return this.referenceModelManager;
	}
	public PersistenceManager getPersistenceManager() {
		return this.persistenceManager;
	}
	public INT004SIIESAPRMProxy getSAPRM() {
		return this.saprm;
	}
	public SEQLEngine getSEQLEngine() {
		return this.seqlEngine;
	}
}
