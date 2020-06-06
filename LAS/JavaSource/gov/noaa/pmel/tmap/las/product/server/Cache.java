package gov.noaa.pmel.tmap.las.product.server;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASBackendResponse;
import gov.noaa.pmel.tmap.las.util.FileListing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;


/** This class manages cached file in a queue. The last accessed file is moved
 * to the head of the queue. If the queue size reached its limit, the least recently
 * accessed file will be removed from the queue and deleted. This class synchronizes
 * access. The class has supporting function for serializing and deserializing cache
 * information to a local file.<p> 
 * @author Yonghua Wei
 */

public class Cache extends LinkedHashMap<String, File> implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -5704886485311957761L;

    public Cache(int cacheSize, long maxBytes){
        super(cacheSize, (float)0.75, true);
        this.cacheSize = cacheSize;
        this.maxBytes = maxBytes;
        this.enabled = true;
    }
    
    /** Test if this cache object is enabled */
    public synchronized boolean isEnabled(){
        return enabled;
    }
    
    /** Enable or disable this cache object according to 
     *  the input parameter <p>
     * @param enabled specified if this cache object should be enabled or disabled.
     */
    public synchronized void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    /** Add a file to the cache if the cache object is enabled.<p>
     *  @param query a string that is the key to the file. The file
     *         can be retrieved using this string.
     *  @param file the File object that need to be stored in cache.
     */
    public synchronized void addFile(String query, File file){
        if(enabled){
            File oldFile = (File)get(query);
            if(oldFile!=null
                    &&!oldFile.getAbsolutePath().equals(file.getAbsolutePath()))
                oldFile.delete();
            put(query, file);
            currentBytes = currentBytes + file.length();
        }
        else{
            file.delete();
        }
    }
    
    /**Get the file identified by a string if the cache object is enabled.<p>
     *
     * @param query a string that is the key to the file. The file
     *         can be retrieved using this string.
     * @param time only a file newer than this time should be returned.
     *         If this parameter is equal to Cache.DELETE_CACHE, cache
     *         file will be deleted and return null. If this parameter is equal to
     *         Cache.GET_CACHE, cache file will always be returned if it
     *         exists.
     * @return a File object that points to the cache file in local 
     *         file system if cache file exists, otherwise return null.
     */ 
    public synchronized File getFile(String query, long time){
        File file = null;
        
        if(enabled){
            file = (File)get(query);
            if(file!=null){
                if(time > 0 && file.exists() 
                        && file.lastModified() >= time){
                    return file;
                }
                else{
                    file.delete();
                    remove(query);
                }
            }
        }
        
        return null;
    }
    
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if(size() > cacheSize || currentBytes > maxBytes ){
            File eldestFile = (File)eldest.getValue();
            currentBytes = currentBytes - eldestFile.length();
            if(eldestFile!=null)
                eldestFile.delete();
            return true;
        }
        else{
            return false;
        }
    }
    
    public void removeFile(String filename) {
    	File gone = (File) get(filename);
    	if ( gone != null ) {
    		currentBytes = currentBytes - gone.length();
    		gone.delete();
    		remove(filename);
    	}
    }
    
    /** Delete all the files in this cache object */
    public synchronized void clean(){
        Iterator it = values().iterator();
        while(it.hasNext()){
            File current = (File)it.next();
            if(current!=null)
                current.delete();
        }
        clear();
        currentBytes = 0l;
    }
    
    
    /** Load a local file and populate this cache object<p>
     * @param cacheFile the local file to load
     */
    public void loadCacheFromStore(File cacheFile) 
    throws LASException {
        File dir = cacheFile.getParentFile();
        if (!cacheFile.exists()) {
            return;
        }
        
        try {
            List<File> cachedFiles = FileListing.getFileListing(dir);
            for (Iterator fileIt = cachedFiles.iterator(); fileIt.hasNext();) {
                File file = (File) fileIt.next();
                if ( !file.getName().equals("lasV7.xml") ) {
                    if ( file.getName().endsWith("_response.xml") ) {
                        LASBackendResponse b = new LASBackendResponse();
                        JDOMUtils.XML2JDOM(file, b);
                        if ( !b.hasError() ) {
                            addFile(file.getAbsolutePath(), file);
                        }
                    } else {
                        addFile(file.getAbsolutePath(), file);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new LASException(e.getMessage());
        } catch (IOException e) {
            throw new LASException(e.getMessage());
        } catch (JDOMException e) {
            throw new LASException(e.getMessage());
        }
    }
    public boolean cacheHit(LASBackendResponse lasResponse) {
        boolean cacheHit = true;
      
        try {
            Element responseRoot = lasResponse.getRootElement();
            List responses = responseRoot.getChildren("response");
            if ( responses.size() == 0 ) {
                cacheHit = false;
            }
            for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
                Element resp = (Element) respIt.next();
                List results = resp.getChildren("result");
                String resultFileName = "";
                for (Iterator rIt = results.iterator(); rIt.hasNext();) {
                    Element result = (Element) rIt.next();
                    String type = result.getAttributeValue("type");
                    // Don't bother to check the RSS file.
                    // It's not needed for a cache hit and is not kept in the cache.
                    if (!type.equals("rss")) {
                        // Don't cache responses that contain a remote result.
                        // TODO explore the efficacy of reading a few bytes from 
                        // the remote source and accepting it as a cache hit
                        String remote = result.getAttributeValue("remote");
                        if (remote != null && remote.equalsIgnoreCase("true")) {
                            cacheHit = false;
                            return cacheHit;
                        }
                        // Don't cache hit on an error response.
                        if (type.equals("error")) {
                            cacheHit = false;
                            return cacheHit;
                        }
                        resultFileName = result.getAttributeValue("file");
                        // Check that each result is in the cache.
                        File resultFile;
                        synchronized (this) {
                            resultFile = this.getFile(resultFileName,
                                    Cache.GET_CACHE);
                        }
                        if (resultFile == null) {
                            cacheHit = false;
                            return cacheHit;
                        }
                    }                    
                }
            }
        } catch (Exception e) {
            cacheHit = false;
            return cacheHit;
        }
        return cacheHit;
    }
    
    public void addToCache(LASBackendResponse lasResponse, String cacheFileName) {   
        if (lasResponse != null) {
        	boolean hasResults = false;
            Element responseRoot = lasResponse.getRootElement();
            List responses = responseRoot.getChildren();
            for (Iterator respIt = responses.iterator(); respIt.hasNext();) {
                Element responseE = (Element) respIt.next();
                List results = responseE.getChildren("result");
                String resultFileName = "";
                for (Iterator rIt = results.iterator(); rIt.hasNext();) {
                    Element result = (Element) rIt.next();
                    if (!result.getAttributeValue("type").equals("error")) {
                        // No error so cache the file.
                        resultFileName = result.getAttributeValue("file");                      
                        // If the result is not a HTTP URL cache it
                        if (!resultFileName.startsWith("http://")) {
                            File resultFile = new File(resultFileName);
                            if ( resultFile.exists() ) {
                            	hasResults = true;
                                this.addFile(resultFileName, resultFile);
                            }
                        }
                    }
                }
            }
            if ( hasResults ) {
                addDocToCache(lasResponse, cacheFileName);
            }
        }
    }
    public void addDocToCache ( LASDocument doc, String cacheFileName) {
    	File docFile = new File(cacheFileName);
    	doc.write(docFile);
    	currentBytes = currentBytes + docFile.length();
    	this.addFile(cacheFileName, docFile);
    }
    public static long DELETE_CACHE = 0;
    
    public static long GET_CACHE = 1;
    
    /** the maximum size of the this cache object in bytes */
    protected long maxBytes;
    
    /** the current size in bytes of the cache */
    protected long currentBytes = 0;
    
    /** the maximum number of cached files. **/
    protected int cacheSize;
    
    /** true if the cache object is enabled, false if not */
    protected boolean enabled;

	/**
	 * @return the maxBytes
	 */
	public long getMaxBytes() {
		return maxBytes;
	}

	/**
	 * @return the cacheSize
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * @param maxBytes the maxBytes to set
	 */
	public void setMaxBytes(long maxBytes) {
		this.maxBytes = maxBytes;
	}

	/**
	 * @param cacheSize the cacheSize to set
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
    /**
     * Get all the files form the cache that are associated with a particular (compound response) key.  It's a bit more complicated 
     * than matching the key string in the name because the response from a compound product might have other files with other keys.  
     * This is handled by parsing through all the response and find the results.
     * @param key
     * @return
     */
	public ArrayList<String> getFiles(String key) {
		ArrayList<String> files = new ArrayList<String>();
		File responseFile = null;
		synchronized(this) {
			for (Iterator fileIt = keySet().iterator(); fileIt.hasNext();) {
				String name = (String) fileIt.next();
				if ( name.contains(key) ) {
					files.add(name);
					if ( name.contains("_response") ) {
						responseFile = get(name);
					} 
				}
			}
		}
		LASBackendResponse las_response = new LASBackendResponse();
		if ( responseFile != null ) {
			try {
				JDOMUtils.XML2JDOM(responseFile, las_response);
			} catch (IOException e) {
				// Don't care.
			} catch (JDOMException e) {
				// Don't care.
			}
			files.addAll(las_response.getResultsAsFiles());
		}
		return files;
	}

	/**
	 * @return the currentBytes
	 */
	public long getCurrentBytes() {
		return currentBytes;
	}

	/**
	 * @param currentBytes the currentBytes to set
	 */
	public void setCurrentBytes(long currentBytes) {
		this.currentBytes = currentBytes;
	}
 
}
