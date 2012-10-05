package com.joey.software.movementToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.framesToolkit.FrameFactroy;


public class MovementPanel extends JPanel implements KeyListener
{
	public static final int iconX = 20;

	public static final int iconY = 20;

	static ImageIcon moveUpIcon = new ImageIcon(DrawTools
			.getMoveUPImage(iconX, iconY));

	static ImageIcon moveDownIcon = new ImageIcon(DrawTools
			.getMoveDownImage(iconX, iconY));

	static ImageIcon moveLeftIcon = new ImageIcon(DrawTools
			.getMoveLeftImage(iconX, iconY));

	static ImageIcon moveRightIcon = new ImageIcon(DrawTools
			.getMoveRightImage(iconX, iconY));

	static ImageIcon removeIcon = new ImageIcon(DrawTools
			.getDeleteImage(iconX, iconY));

	static ImageIcon scaleUpIcon = new ImageIcon(DrawTools
			.getAddImage(iconX, iconY));

	static ImageIcon scaleDownIcon = new ImageIcon(DrawTools
			.getRemoveImage(iconX, iconY));

	public static final int MOVE_UP_INDEX = 0;

	public static final int MOVE_DOWN_INDEX = 1;

	public static final int MOVE_LEFT_INDEX = 2;

	public static final int MOVE_RIGHT_INDEX = 3;

	public static final int SCALE_DOWN_INDEX = 4;

	public static final int SCALE_UP_INDEX = 5;

	public static final int REMOVE_INDEX = 6;

	public static final int KEY_TYPE_NUMPAD = 0;

	public static final int KEY_TYPE_KEYBOARD = 1;

	int[] keys = new int[8];

	JButton moveUp = new JButton(moveUpIcon);

	JButton moveDown = new JButton(moveDownIcon);

	JButton moveLeft = new JButton(moveLeftIcon);

	JButton moveRight = new JButton(moveRightIcon);

	JButton remove = new JButton(removeIcon);

	JButton scaleUp = new JButton(scaleUpIcon);

	JButton scaleDown = new JButton(scaleDownIcon);

	JButton startKeyBoardListen = new JButton("K");

	JButton startNumPadListen = new JButton("N");

	boolean useMoveUp = true;

	boolean useMoveDown = true;

	boolean useMoveLeft = true;

	boolean useMoveRight = true;

	boolean useRemove = true;

	boolean useScaleUp = true;

	boolean useScaleDown = true;

	boolean showSettings = true;

	JSpinner moveSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0, 1e10, 1));

	JSpinner scaleSpinner = new JSpinner(
			new SpinnerNumberModel(1.0, 0, 1e10, 1));

	JPanel moveControlPanel = new JPanel();

	JPanel settingsPanel = new JPanel();

	ArrayList<MovementPanelInterface> listners = new ArrayList<MovementPanelInterface>();

	public MovementPanel()
	{
		createJPanel();
		setKeyType(KEY_TYPE_KEYBOARD);
	}

	public void setShowSettings(boolean show)
	{
		this.showSettings = show;
		removeAll();

		if (showSettings)
		{
			setLayout(new BorderLayout());
			add(moveControlPanel, BorderLayout.CENTER);
			add(settingsPanel, BorderLayout.NORTH);
		} else
		{
			setLayout(new BorderLayout());
			add(moveControlPanel, BorderLayout.CENTER);
		}
	}

	public void setKeyType(int type)
	{
		if (type == KEY_TYPE_NUMPAD)
		{

			keys = new int[]
			{ KeyEvent.VK_NUMPAD8, // Up
					KeyEvent.VK_NUMPAD2, // Down
					KeyEvent.VK_NUMPAD4, // Left
					KeyEvent.VK_NUMPAD6, // Right
					KeyEvent.VK_NUMPAD7, // Scale Down
					KeyEvent.VK_NUMPAD9, // Scale Up
					KeyEvent.VK_NUMPAD5 // Delete Icon
			};
		} else if (type == KEY_TYPE_KEYBOARD)
		{
			keys = new int[]
			{ KeyEvent.VK_W, // Up
					KeyEvent.VK_S, // Down
					KeyEvent.VK_A, // Left
					KeyEvent.VK_D, // Right
					KeyEvent.VK_Q, // Scale Down
					KeyEvent.VK_E, // Scale Up
					KeyEvent.VK_X // Delete Icon
			};
		}
	}

	public void removeAllListners()
	{
		listners.clear();
	}

	public void removeListner(MovementPanelInterface listner)
	{
		listners.remove(listner);
	}

	public void addListner(MovementPanelInterface listner)
	{
		listners.add(listner);
	}

	public void setKeyListnerFocus()
	{
		grabFocus();
	}

	public void createJPanel()
	{
		addKeyListener(this);
		setBorder(BorderFactory.createTitledBorder("Movement Controler"));
		setPreferredSize(new Dimension(150, 250));
		moveControlPanel.removeAll();
		moveControlPanel
				.setBorder(BorderFactory.createTitledBorder("Movement"));
		moveControlPanel.setPreferredSize(new Dimension(iconX * 3, iconY * 3));
		moveControlPanel.setLayout(new GridLayout(3, 3));
		moveControlPanel.add(scaleUp);
		moveControlPanel.add(moveUp);
		moveControlPanel.add(scaleDown);
		moveControlPanel.add(moveLeft);
		moveControlPanel.add(remove);
		moveControlPanel.add(moveRight);
		moveControlPanel.add(startKeyBoardListen);
		moveControlPanel.add(moveDown);
		moveControlPanel.add(startNumPadListen);

		JPanel movePanel = new JPanel(new BorderLayout());
		JLabel moveLabel = new JLabel("Move :  ");
		movePanel.add(moveLabel, BorderLayout.WEST);
		movePanel.add(moveSpinner, BorderLayout.CENTER);

		JLabel scaleLabel = new JLabel("Scale : ");
		JPanel scalePanel = new JPanel(new BorderLayout());
		scalePanel.add(scaleLabel, BorderLayout.WEST);
		scalePanel.add(scaleSpinner, BorderLayout.CENTER);

		settingsPanel.removeAll();
		settingsPanel.setLayout(new GridLayout(2, 1));
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		settingsPanel.add(scalePanel);
		settingsPanel.add(movePanel);

		// This is where the panes will be added to this
		setShowSettings(true);

		startKeyBoardListen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setKeyType(KEY_TYPE_KEYBOARD);
				setKeyListnerFocus();
			}
		});

		startNumPadListen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setKeyType(KEY_TYPE_NUMPAD);
				setKeyListnerFocus();
			}
		});

		moveUp.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				moveUpPressed();
			}
		});

		moveDown.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				moveDownPressed();
			}
		});

		moveLeft.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				moveLeftPressed();

			}
		});

		moveRight.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				moveRightPressed();
			}
		});

		scaleDown.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				scaleDownPressed();
			}
		});

		scaleUp.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				scaleUpPressed();
			}
		});

		remove.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				removePressed();
			}
		});
	}

	public void moveUpPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.moveUpPressed(this);
		}
	}

	public void moveDownPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.moveDownPressed(this);
		}
	}

	public void moveLeftPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.moveLeftPressed(this);
		}
	}

	public void moveRightPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.moveRightPressed(this);
		}
	}

	public void scaleUpPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.scaleUpPressed(this);
		}
	}

	public void scaleDownPressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.scaleDownPressed(this);
		}
	}

	public void removePressed()
	{
		for (MovementPanelInterface i : listners)
		{
			i.removePressed(this);
		}
	}

	public void updateButtons()
	{
		scaleUp.setVisible(useScaleUp);
		scaleDown.setVisible(useScaleDown);
		remove.setVisible(useRemove);
		moveUp.setVisible(useMoveUp);
		moveDown.setVisible(useMoveDown);
		moveLeft.setVisible(useMoveLeft);
		moveRight.setVisible(useMoveRight);
		updateUI();
	}

	public static void main(String input[])
	{
		MovementPanel move = new MovementPanel();

		FrameFactroy.getFrame(move);
	}

	public boolean isUseMoveUp()
	{
		return useMoveUp;
	}

	public void setUseMoveUp(boolean useMoveUp)
	{
		this.useMoveUp = useMoveUp;
		updateButtons();
	}

	public boolean isUseMoveDown()
	{
		return useMoveDown;
	}

	public void setUseMoveDown(boolean useMoveDown)
	{
		this.useMoveDown = useMoveDown;
		updateButtons();
	}

	public boolean isUseMoveLeft()
	{
		return useMoveLeft;
	}

	public void setUseMoveLeft(boolean useMoveLeft)
	{
		this.useMoveLeft = useMoveLeft;
		updateButtons();
	}

	public boolean isUseMoveRight()
	{
		return useMoveRight;
	}

	public void setUseMoveRight(boolean useMoveRight)
	{
		this.useMoveRight = useMoveRight;
		updateButtons();
	}

	public boolean isUseRemove()
	{
		return useRemove;
	}

	public void setUseRemove(boolean useRemove)
	{
		this.useRemove = useRemove;
		updateButtons();
	}

	public boolean isUseScaleUp()
	{
		return useScaleUp;
	}

	public void setUseScaleUp(boolean useScaleUp)
	{
		this.useScaleUp = useScaleUp;
		updateButtons();
	}

	public boolean isUseScaleDown()
	{
		return useScaleDown;
	}

	public void setUseScaleDown(boolean useScaleDown)
	{
		this.useScaleDown = useScaleDown;
		updateButtons();
	}

	public double getMovementAmount()
	{
		return (Double) moveSpinner.getValue();
	}

	public void setMovementAmount(double movementAmount)
	{
		moveSpinner.setValue(movementAmount);
	}

	public double getScaleIncrement()
	{
		return (Double) scaleSpinner.getValue();
	}

	public void setScaleIncrement(double scaleIncrement)
	{
		scaleSpinner.setValue(scaleIncrement);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();

		if (code == keys[MOVE_UP_INDEX])
		{
			moveUpPressed();
		} else if (code == keys[MOVE_DOWN_INDEX])
		{
			moveDownPressed();
		} else if (code == keys[MOVE_LEFT_INDEX])
		{
			moveLeftPressed();
		} else if (code == keys[MOVE_RIGHT_INDEX])
		{
			moveRightPressed();
		} else if (code == keys[REMOVE_INDEX])
		{
			removePressed();
		} else if (code == keys[SCALE_UP_INDEX])
		{
			scaleUpPressed();
		} else if (code == keys[SCALE_DOWN_INDEX])
		{
			scaleDownPressed();
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}
}
