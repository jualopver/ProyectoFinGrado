package isaFoundry.core;


import isaFoundry.contentManager.ContentManager;
import isaFoundry.email.EmailService;
import isaFoundry.processEngine.ProccesEngine;
import isaFoundry.processEngine.UserTaskRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Core {

	private static Logger			Log			= LoggerFactory.getLogger(Core.class);
	private static ContentManager	cManager	= new ContentManager();
	private static ProccesEngine	pEngine		= new ProccesEngine();
	private static EmailService		eService	= new EmailService();

	public Core() {}

	/**
	 * Copia un documento desde una ruta a otra.
	 * 
	 * @param filePath
	 *            ruta completa del archivo
	 * @param destinationPath
	 *            carpeta destino
	 */
	public static void copyDoc(String filePath, String destinationPath, String name) {
		cManager.copyDoc(filePath , destinationPath, name);
	}

	/**
	 * Copia un documento desde una ruta a otra.
	 * 
	 * @param fileName
	 *            nombre del archivo
	 * @param sourcePath
	 *            carpeta origen
	 * @param destinationPath
	 *            carpeta destino
	 */
	/*
	public static void copyDoc(String fileName, String sourcePath, String destinationPath) {
		cManager.copyDoc(fileName , sourcePath , destinationPath);
	}
	*/

	public static void doTasks(List<UserTaskRequest> lt) {
		pEngine.doTasks(lt);
	}

	/**
	 * Crea un nuevo directorio en el repositorio
	 * 
	 * @param path
	 *            ruta del directorio
	 */
	public static String newFolder(String path) {
		return cManager.newFolder(path).getPath();
	}

	public static void run() {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {

			public void run() {
				Core.Log.info("loop ejecutandose...");
				List<UserTaskRequest> lista = eService.taskReceived();
				Core.doTasks(lista);
				for (UserTaskRequest task : lista) {
					Core.Log.info("IdTask: " + task.idTask + "; Action: " + task.action);
				}
				// TODO:Comprobar tareas pendientes en el motor de activiti
				// TODO:Comprobar emails y formularios para realizar tareas
				// pendientes
			}
			
			//cambiamos de MINUTES a SECONDS
		} , 0 , 30 , TimeUnit.SECONDS);
	}

	/**
	 * Realiza el envio de un correo mediante la informacion proporcionada en
	 * xml.
	 * 
	 * @param templatePath
	 *            incluye toda la informacion del correo a enviar.
	 */
	public static void sendEmail(String subject, String body, List<String> tos) {
		eService.SendEmail(subject , body , tos);
	}

	public static void startProcces(String procesKey, Map<String, Object> var) {
		pEngine.startProces(procesKey , var);
	}

	/**
	 * Crea un documento pdf mediante una carpeta intermedia con regla de
	 * conversión asociada
	 * 
	 * @param fileName
	 *            nombre del documento
	 * @param sourcePath
	 *            carpeta origen del documento
	 * @param targetPath
	 *            carpeta destino del documento pdf
	 * @param converterPath
	 *            ruta de la carpeta de transformación
	 */
	public static void toPDF(String fileName, String sourcePath, String targetPath, String converterPath) {
		cManager.toPDF(fileName , sourcePath , targetPath , converterPath);
	}

	/**
	 * Recupera la url del documento.
	 * 
	 * @param doc
	 *            ruta al documento
	 * @return String que representa la url del documento en nuestro repositorio
	 */
	public static String urlDoc(String doc) {
		return cManager.getDocumentURL(doc);
	}

	/**
	 * Recupera la url para la edicion del documento en linea.
	 * 
	 * @param doc
	 * @return String que representa la url de acceso a edición mediante google
	 *         docs en nuestro repositorio
	 */
	public static String urlDocOnlineEdit(String doc) {
		return cManager.getOnlineEditURL(doc);
	}
}
