package org.sigaim.siie.ws;

import java.io.ByteArrayInputStream;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openehr.am.parser.ContentObject;
import org.sigaim.siie.interfaces.reportmanagement.ReturnValueCreateHealthcareFacility;
import org.sigaim.siie.interfaces.reportmanagement.ReturnValueCreatePerformer;
import org.sigaim.siie.interfaces.reportmanagement.ReturnValueCreateReport;
import org.sigaim.siie.interfaces.reportmanagement.ReturnValueCreateSubjectOfCare;
import org.sigaim.siie.interfaces.reportmanagement.ReturnValueUpdateReport;
import org.sigaim.siie.iso13606.rm.CDCV;
import org.sigaim.siie.iso13606.rm.FunctionalRole;
import org.sigaim.siie.iso13606.rm.II;
import org.sigaim.siie.iso13606.rm.VersionStatus;
import org.sigaim.siie.rm.exceptions.CSReason;
import org.sigaim.siie.rm.exceptions.ReferenceModelException;
import org.sigaim.siie.rm.exceptions.RejectException;

@WebService
public class INTSIIE004ReportManagementImpl {
	
	protected <T extends Object> T bindFromDADL(String dadl, Class<T> type) throws ReferenceModelException {
		ContentObject parsedDADL=ServiceInjector.getInstance().getDADLManager().parseDADL(new ByteArrayInputStream(dadl.getBytes()));
		return  type.cast(ServiceInjector.getInstance().getReferenceModelManager().bind(parsedDADL));
	}
	
	@WebMethod
	public ReturnValueCreateHealthcareFacility createHealthcareFacility(String requestId) {
		ReturnValueCreateHealthcareFacility ret=null;
		try {
			ret=ServiceInjector.getInstance().getIntSIIE004ReportManagement().createHealthcareFacility(requestId);
		} catch(RejectException e) {
			ret=new ReturnValueCreateHealthcareFacility();
			ret.setRequestId(requestId);
			ret.setReasonCode(e.getReason().toString());
		}
		return ret;
	}
	@WebMethod
	public ReturnValueCreateSubjectOfCare createSubjectOfCare(String requestId) {
		ReturnValueCreateSubjectOfCare ret=null;
		try {
			ret=ServiceInjector.getInstance().getIntSIIE004ReportManagement().createSubjectOfCare(requestId);
		} catch(RejectException e) {
			ret=new ReturnValueCreateSubjectOfCare();
			ret.setRequestId(requestId);
			ret.setReasonCode(e.getReason().toString());
		}
		return ret;
	}
	@WebMethod
	public ReturnValueCreatePerformer createPerformer(String requestId)  {
		ReturnValueCreatePerformer ret=null;
		try {
			ret=ServiceInjector.getInstance().getIntSIIE004ReportManagement().createPerformer(requestId);
		} catch(RejectException e) {
			ret=new ReturnValueCreatePerformer();
			ret.setRequestId(requestId);
			ret.setReasonCode(e.getReason().toString());
		}
		return ret;
	}
	@WebMethod
	public ReturnValueCreateReport createReport(
			String requestId,
			String subjectOfCareId, //II
			String composerId, //FunctionalRole
			String audioData,
			String textTranscription,
			String reportStatus, //CDCV
			String rootArchetypeId //II
			)  {
		ReturnValueCreateReport ret=null;
		try {
			ret=ServiceInjector.getInstance().getIntSIIE004ReportManagement().createReport(requestId, bindFromDADL(subjectOfCareId,II.class), bindFromDADL(composerId,FunctionalRole.class), audioData, textTranscription, bindFromDADL(reportStatus,CDCV.class), bindFromDADL(rootArchetypeId,II.class));
		} catch(RejectException e) {
			ret=new ReturnValueCreateReport();
			ret.setRequestId(requestId);
			ret.setReasonCode(e.getReason().toString());
		} catch(Exception e) {
			ret=new ReturnValueCreateReport();
			ret.setRequestId(requestId);
			ret.setReasonCode(CSReason.REAS02.toString());
		}
		return ret;
	}
}
