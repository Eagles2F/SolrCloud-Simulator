package edu.cmu.ece845.utility;

import edu.cmu.ece845.loadbalancer.NodeListener;

// giving credit to http://www.javacoffeebreak.com/articles/network_timeouts/
public class HeartbeatTimer extends Thread{
	/** Rate at which timer is checked */
	protected int m_rate = 100;
	
	/** Length of timeout */
	private int m_length;

	/** Time elapsed */
	private int m_elapsed;
	
	public NodeListener nl;
	
	/**
	  * Creates a timer of a specified length
	  * @param	length	Length of time before timeout occurs
	  */
	public HeartbeatTimer (NodeListener n,int length)
	{
		// Assign to member variable
		m_length = length;

		// Set time elapsed
		m_elapsed = 0;
		
		nl = n;
	}

	
	/** Resets the timer back to zero */
	public synchronized void reset()
	{
		m_elapsed = 0;
	}

	/** Performs timer specific code */
	public void run()
	{
		// Keep looping
		for (;;)
		{
			// Put the timer to sleep
			try
			{ 
				Thread.sleep(m_rate);
			}
			catch (InterruptedException ioe) 
			{
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				m_elapsed += m_rate;

				// Check to see if the time has been exceeded
				if (m_elapsed > m_length)
				{
					// Trigger a timeout
					timeout();
				}
			}

		}
	}

	// Override this to provide custom functionality
	public void timeout()
	{
		
	}
}
