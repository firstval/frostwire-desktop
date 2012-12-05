/*
 * Created by Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.mplayer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.SwingUtilities;

import org.limewire.util.CommonUtils;

import com.apple.eawt.CocoaComponent;
import com.frostwire.gui.player.AudioSource;
import com.frostwire.gui.player.MPlayerUIEventHandler;
import com.frostwire.gui.player.MediaPlayer;
import com.frostwire.gui.player.MediaPlayerListener;
import com.frostwire.mplayer.MediaPlaybackState;

/**
 * @author aldenml
 *
 */
public class MPlayerComponentOSX extends CocoaComponent implements MPlayerComponent, MediaPlayerListener {

    private static final long serialVersionUID = -8610816510893757828L;

    private static final int JMPlayer_addNotify = 1;
    private static final int JMPlayer_dispose = 2;
    
    // ui events
    private static final int JMPlayer_volumeChanged = 3;
    private static final int JMPlayer_timeInitialized = 4;
    private static final int JMPlayer_progressChanged = 5;
    private static final int JMPlayer_stateChanged = 6;
    private static final int JMPlayer_toggleFS = 7;
    
    // states
    private static final int JMPlayer_statePlaying = 1;
    private static final int JMPlayer_statePaused = 2;
    private static final int JMPlayer_stateClosed = 3;
    
    private boolean refreshPlayTime = false;
    
    private long nsObject = 0;

    public MPlayerComponentOSX() {
    	MediaPlayer.instance().addMediaPlayerListener(this);
    }

    @Override
    public int createNSView() {
        return (int) createNSViewLong();
    }

    @Override
    public long createNSViewLong() {
        com.apple.concurrent.Dispatch.getInstance().getBlockingMainQueueExecutor().execute(new Runnable() {
            @Override
            public void run() {
                nsObject = createNSView1(CommonUtils.getExecutableDirectory());
            }
        });

        return nsObject;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(25, 25);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250, 250);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        sendMsg(JMPlayer_addNotify);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
    
    @Override
    public boolean toggleFullScreen() {
        sendMsg(JMPlayer_toggleFS);
        return true;
    }
    
    @Override
	public long getWindowID() {
		return 0;  // always returns 0 on OSX
	}
    
    protected void dispose() {
        sendMsg(JMPlayer_dispose);
    }

    private void sendMsg(int messageID) {
        if (nsObject != 0) {
            sendMessage(messageID, null);
        }
    }

    private void sendMsg(int messageID, Object message) {
        if (nsObject != 0) {
            sendMessage(messageID, message);
            
        }
    }

    
    private native long createNSView1(String appPath);
    
    /*
     * JNI Hook methods - forward all to the UIEventHandler
     */
	public void onVolumeChanged(final float volume) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onVolumeChanged(volume);
			}
		});
	}
	
	public void onIncrementVolumePressed() {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onVolumeIncremented();
			}
		});
	}
	
	public void onDecrementVolumePressed() {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onVolumeDecremented();
			}
		});
	}
    
	public void onSeekToTime(final float seconds) {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onSeekToTime(seconds);
			}
    	});
    }
    public void onPlayPressed() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onPlayPressed();
			}
    	});
    }
    public void onPausePressed() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onPausePressed();
			}
    	});
    }
    public void onFastForwardPressed() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onFastForwardPressed();
			}
    	});
    }
    public void onRewindPressed() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onRewindPressed();
			}
    	});
    }
    public void onToggleFullscreenPressed() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onToggleFullscreenPressed();
			} 
		});		
    }
    
    public void onProgressSliderStarted() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onProgressSlideStart();
			} 
		});
    }
    
    public void onProgressSliderEnded() {
    	SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MPlayerUIEventHandler.instance().onProgressSlideEnd();
			} 
		});
    }

    /*
     * MediaPlayer Listener events - to send back to the native side.
     */
	@Override
	public void mediaOpened(MediaPlayer audioPlayer, AudioSource audioSource) {}

	@Override
	public void progressChange(MediaPlayer audioPlayer, float currentTimeInSecs) {
		sendMsg(JMPlayer_progressChanged, Float.valueOf(currentTimeInSecs));
	}

	@Override
	public void volumeChange(MediaPlayer audioPlayer, double currentVolume) {
		sendMsg(JMPlayer_volumeChanged, Float.valueOf((float)currentVolume));
	}

	@Override
	public void stateChange(MediaPlayer audioPlayer, MediaPlaybackState state) {
		int s;
		
		switch(state) {
		case Playing : 
			s=JMPlayer_statePlaying; 
			break;
		case Paused: 
			s=JMPlayer_statePaused; 
			break;
		case Closed: 
			s=JMPlayer_stateClosed; 
			break;
		default: 
			s=-1; 
			break;
		}
		
		if ( state == MediaPlaybackState.Playing && refreshPlayTime ) {
			refreshPlayTime = false;
			sendMsg(JMPlayer_timeInitialized, Float.valueOf(MediaPlayer.instance().getDurationInSecs()));
		}
		
		if ( state != MediaPlaybackState.Playing ) {
			refreshPlayTime = true;
		}
		
		if ( s != -1 ) {
			sendMsg(JMPlayer_stateChanged, Integer.valueOf(s));
		}
	}

	@Override
	public void icyInfo(MediaPlayer audioPlayer, String data) {}
}