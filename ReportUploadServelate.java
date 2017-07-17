package test.automation.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * 
 * @author Chirag Jayswal
 *
 */
public class ReportUploadServelate extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2235350020355316100L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		writeFile(request, response);
	}

	private String getFileName(final Part part) {
		
		final String partHeader = part.getHeader("content-disposition");
		for (String content : partHeader.split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
	
	private void writeFile(HttpServletRequest request,ServletResponse response) throws IOException, ServletException{
		
		Part filePart = request.getPart("file");
		final String fileName = getFileName(filePart);
	    OutputStream out = null;
	    InputStream filecontent = null;
	    final PrintWriter writer = response.getWriter();
        String path = "uploadHolder";
    	final File upload = new File(path);
        final File file = new File("test-results", fileName);

	    try {
			if (!upload.exists() && !upload.mkdirs()) {
				throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
			}
			out = new FileOutputStream(file);
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        
			new Thread(new Runnable() {
				
				@Override
				public void run() {
						new ReportProcessor().processZip(file);
					
					System.out.printf(Thread.currentThread().getName()+">> File %s being uploaded to %s", 
			                new Object[]{fileName, path}); 
				}
			}).start();
			
	        writer.println("Uploaded file " + fileName + " at " + path);

	    } catch (FileNotFoundException fne) {
	        writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");
	        writer.println("<br/> ERROR: " + fne.getMessage());

	        System.out.printf("Problems during file upload. Error: %s", 
	                new Object[]{fne.getMessage()});
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        if (writer != null) {
	            writer.close();
	        }
	    }
	}

}
