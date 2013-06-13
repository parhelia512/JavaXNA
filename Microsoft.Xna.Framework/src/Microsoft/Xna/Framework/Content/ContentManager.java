package Microsoft.Xna.Framework.Content;

import java.lang.reflect.*;
import java.util.*;

import Microsoft.Xna.Framework.Graphics.*;
import System.*;

/**
 * The ContentManager is the run-time component which loads managed objects from the binary files produced by the design time content pipeline. It also manages the
 * lifespan of the loaded objects, disposing the content manager will also dispose any assets which are themselves IDisposable.
 * 
 * @author Halofreak1990
 */
public class ContentManager implements IDisposable
{
	private HashMap<String, Object> assets;
	private boolean disposed;
	private String rootDirectory;
	private IServiceProvider serviceProvider;
	private Action<IDisposable> recordDisposableObject;

	public String getRootDirectory()
	{
		return this.rootDirectory;
	}
	
	public void setRootDirectory(String value)
	{
		if (value == null)
			throw new ArgumentNullException("value");
		
		if (this.assets.size() > 0)
			throw new InvalidOperationException("");
		
		this.rootDirectory = value;
		/*this.fullRootDirectory = value;
        this.isRootDirectoryAbsolute = TitleContainer.IsPathAbsolute(value);
        if (this.isRootDirectoryAbsolute)
        {
            try
            {
                this.fullRootDirectory = Path.Combine(TitleLocation.Path, value);
            }
            catch (RuntimeException ex)
            {
            }
        }*/
	}
	
	public IServiceProvider getServiceProvider()
	{
		return this.serviceProvider;
	}
	
	/**
	 * 
	 * @param serviceProvider
	 * @throws ArgumentNullException
	 */
	public ContentManager(IServiceProvider serviceProvider)
	{
		this(serviceProvider, "");
	}
	
	/**
	 * 
	 * @param serviceProvider
	 * @param rootDirectory
	 * @throws ArgumentNullException
	 */
	public ContentManager(IServiceProvider serviceProvider, String rootDirectory)
	{
		if (serviceProvider == null)
			throw new ArgumentNullException("serviceProvider");
		
		if (rootDirectory == null)
			throw new ArgumentNullException("rootDirectory");
		
		this.assets = new HashMap<String, Object>();
		this.rootDirectory = rootDirectory;
		this.serviceProvider = serviceProvider;
	}
	
	/**
	 * 
	 */
	@Override
	public void Dispose()
	{
		Dispose(true);
	}
	
	protected void Dispose(boolean disposing)
	{
		if (!this.disposed)
		{
			if (disposing)
			{
				// Dispose any managed resources
				Unload();
			}
			// Dispose any unmanaged resources
			this.disposed = true;
		}
	}
	
	/**
	 * 
	 * @param <T>
	 * @param assetName
	 * @return
	 * @throws ArgumentNullException
	 * @throws ObjectDisposedException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T Load(String assetName)
	{
		Class cl = this.getClass();
		Type t = null;
		
		try
		{
			Method m = cl.getMethod("Load", String.class);
			t = m.getGenericReturnType();
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e)	{ e.printStackTrace(); }
		
		if (this.disposed)
			throw new ObjectDisposedException(super.toString());
		
		if (assetName == null || assetName == "")
			throw new ArgumentNullException("assetName");
		
		if (this.assets.containsKey(assetName))
		{
			try
			{
				return (T)this.assets.get(assetName);
			}
			catch (ClassCastException e)
			{
				
			}
		}
		
		T asset = this.ReadAsset(assetName, null);
		return asset;
	}
	
	/**
	 * Low-level worker method that reads asset data.
	 * 
	 * @param <T>
	 * @param assetName
	 * The name of the asset to be loaded from disk.
	 * 
	 * @param recordDisposableObject
	 * Delegate function for handling the disposition of assets. If recordDisposableObject is null,
	 * the default lifespan tracking and management is used, so unloading or disposing of the content
	 * manager frees everything that has been loaded through it. If recordDisposableObject specifies 
	 * a valid delegate, that delegate is used instead of the default lifespan tracking and is called 
	 * every time the loader encounters a type that implements IDisposable. You must use your own code 
	 * to unload assets loaded in this fashion, since ContentManager's Unload method will not be aware
	 * of them.
	 */
	protected <T> T ReadAsset(String assetName, Action<IDisposable> recordDisposableObject)
	{
		if (this.disposed)
			throw new ObjectDisposedException(this.getClass().getName());
		
		if (assetName == null || assetName == "")
			throw new ArgumentNullException("assetName");
		
		this.recordDisposableObject = recordDisposableObject;
		
		throw new NotImplementedException();
	}
	
	/**
	 * 
	 * @throws ObjectDisposedException
	 */
	public void Unload()
	{
		if (this.disposed)
			throw new ObjectDisposedException(this.getClass().getName());
		
		Collection<Object> c = assets.values();
		Iterator<Object> enumerator = c.iterator();
		
		if (recordDisposableObject != null)
		{
			while(enumerator.hasNext())
			{
				try { recordDisposableObject.Invoke((IDisposable)enumerator.next()); }
				catch (ClassCastException e) { }
			}
		}
		else
		{
			while (enumerator.hasNext())
			{
				try { ((IDisposable)enumerator.next()).Dispose(); }
				catch (ClassCastException e) { }
			}
		}
		this.assets.clear();
		this.disposed = true;
	}
}
