package com.joey.software.DataToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.joey.software.mathsToolkit.NumberDimension;


/**
 * This code was edited or generated using
 * CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo
 * is being used commercially (ie, by a
 * corporation, company or business for any
 * purpose whatever) then you should purchase a
 * license for each developer using Jigloo. Please
 * visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing
 * terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS
 * CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE
 * OR COMMERCIAL PURPOSE.
 */
public class OCTDataSetPanel extends JPanel
{
	NativeDataSet dataSet;

	private JPanel jPanel18;

	private JPanel fileLocationPanel;

	private JPanel jPanel17;

	private JPanel jPanel16;

	private JPanel jPanel15;

	private JPanel jPanel14;

	private JPanel jPanel13;

	private JPanel jPanel12;

	private JPanel jPanel11;

	private JPanel jPanel10;

	private JPanel jPanel9;

	NumberDimension xSize = new NumberDimension("m");

	NumberDimension ySize = new NumberDimension("m");

	NumberDimension zSize = new NumberDimension("m");

	JButton editSize = new JButton("Edit");

	JTextArea infomation = new JTextArea();

	private JScrollPane jScrollPane1;

	private JLabel jLabel4;

	private JLabel jLabel5;

	private JLabel jLabel7;

	private JTextField sizePreviewX;

	private JTextField sizePreviewY;

	private JTextField sizePreviewZ;

	private JLabel jLabel6;

	private JPanel jPanel4;

	private JTextField sizeDataX;

	private JPanel jPanel8;

	private JPanel jPanel7;

	private JPanel jPanel6;

	private JPanel jPanel5;

	private JLabel jLabel8;

	private JTextField sizeDataY;

	private JTextField sizeDataZ;

	private JLabel jLabel3;

	private JPanel jPanel3;

	private JPanel jPanel2;

	private JLabel jLabel2;

	private JLabel jLabel1;

	private JLabel sizeDataXLabel;

	private JPanel jPanel1;

	boolean saveData = true;

	public OCTDataSetPanel()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		xSize.setNumberEditable(false);
		ySize.setNumberEditable(false);
		zSize.setNumberEditable(false);

		xSize.setIgnorePowerChange(false);
		ySize.setIgnorePowerChange(false);
		zSize.setIgnorePowerChange(false);

		xSize.setNumberformat("#,###.###");
		ySize.setNumberformat("#,###.###");
		zSize.setNumberformat("#,###.###");
		BorderLayout thisLayout = new BorderLayout();
		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(513, 397));
		{
			jPanel5 = new JPanel();
			GridLayout jPanel5Layout = new GridLayout(1, 3);
			jPanel5Layout.setColumns(3);
			jPanel5Layout.setHgap(5);
			jPanel5Layout.setVgap(5);
			jPanel5.setLayout(jPanel5Layout);
			this.add(jPanel5, BorderLayout.NORTH);
			jPanel5.setSize(783, 100);
			jPanel5.setMaximumSize(new java.awt.Dimension(32767, 300));
			jPanel5.setPreferredSize(new java.awt.Dimension(513, 140));
			{
				jPanel15 = new JPanel();
				BorderLayout jPanel15Layout = new BorderLayout();
				jPanel15.setLayout(jPanel15Layout);
				jPanel5.add(jPanel15);
				jPanel15.setPreferredSize(new java.awt.Dimension(167, 155));
				jPanel15.setBorder(BorderFactory
						.createTitledBorder("Data Dimensions"));
				{
					jPanel1 = new JPanel();
					jPanel15.add(jPanel1, BorderLayout.NORTH);
					GridLayout jPanel1Layout = new GridLayout(4, 1);
					jPanel1Layout.setColumns(1);
					jPanel1Layout.setHgap(5);
					jPanel1Layout.setVgap(5);
					jPanel1Layout.setRows(4);
					jPanel1.setLayout(jPanel1Layout);
					jPanel1.setBounds(19, 18, 208, 132);
					jPanel1.setSize(257, 100);
					{
						jPanel6 = new JPanel();
						BorderLayout jPanel6Layout = new BorderLayout();
						jPanel6.setLayout(jPanel6Layout);
						jPanel1.add(jPanel6);
						jPanel6.setPreferredSize(new java.awt.Dimension(103, 20));
						{
							sizeDataXLabel = new JLabel();
							jPanel6.add(sizeDataXLabel, BorderLayout.WEST);
							sizeDataXLabel.setText("Dim X :");
							sizeDataXLabel
									.setHorizontalTextPosition(SwingConstants.CENTER);
							sizeDataXLabel
									.setHorizontalAlignment(SwingConstants.RIGHT);
							jPanel6.add(xSize, BorderLayout.CENTER);
						}
					}
					{
						jPanel8 = new JPanel();
						jPanel1.add(jPanel8);
						BorderLayout jPanel8Layout = new BorderLayout();
						jPanel8.setLayout(jPanel8Layout);
						jPanel8.setPreferredSize(new java.awt.Dimension(75, 20));

						jPanel8.add(ySize, BorderLayout.CENTER);
					}
					{
						jPanel7 = new JPanel();
						jPanel1.add(jPanel7);
						BorderLayout jPanel7Layout = new BorderLayout();
						jPanel7.setLayout(jPanel7Layout);
						jPanel7.setPreferredSize(new java.awt.Dimension(123, 20));
						jPanel7.add(zSize, BorderLayout.CENTER);
					}
					{
						jLabel2 = new JLabel();
						jPanel7.add(jLabel2, BorderLayout.WEST);
						jLabel2.setText("Dim Z :");
						jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
						jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
					}
					{
						jLabel1 = new JLabel();
						jPanel8.add(jLabel1, BorderLayout.WEST);
						jLabel1.setText("Dim Y :");
						jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
						jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
					}
					jPanel1.add(editSize);
				}
			}
			{
				jPanel16 = new JPanel();
				BorderLayout jPanel16Layout = new BorderLayout();
				jPanel16.setLayout(jPanel16Layout);
				jPanel5.add(jPanel16);
				jPanel16.setPreferredSize(new java.awt.Dimension(167, 155));
				jPanel16.setBorder(BorderFactory
						.createTitledBorder("Raw Data Size"));
				{
					jPanel3 = new JPanel();
					jPanel16.add(jPanel3, BorderLayout.NORTH);
					GridLayout jPanel3Layout = new GridLayout(3, 1);
					jPanel3Layout.setColumns(1);
					jPanel3Layout.setHgap(5);
					jPanel3Layout.setVgap(5);
					jPanel3Layout.setRows(3);
					jPanel3.setLayout(jPanel3Layout);
					jPanel3.setBounds(235, 18, 208, 132);
					jPanel3.setSize(257, 100);
					{
						jPanel11 = new JPanel();
						jPanel3.add(jPanel11);
						BorderLayout jPanel11Layout = new BorderLayout();
						jPanel11.setLayout(jPanel11Layout);
						{
							sizeDataX = new JTextField();
							jPanel11.add(sizeDataX, BorderLayout.CENTER);
						}
					}
					{
						jPanel10 = new JPanel();
						jPanel3.add(jPanel10);
						BorderLayout jPanel10Layout = new BorderLayout();
						jPanel10.setLayout(jPanel10Layout);
						{
							jLabel3 = new JLabel();
							jPanel10.add(jLabel3, BorderLayout.WEST);
							jLabel3.setText("Size Y : ");
							jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
							jLabel3.setHorizontalTextPosition(SwingConstants.CENTER);
						}
						{

							sizeDataY = new JTextField();
							jPanel10.add(sizeDataY, BorderLayout.CENTER);
						}
					}
					{
						jPanel9 = new JPanel();
						BorderLayout jPanel9Layout = new BorderLayout();
						jPanel9.setLayout(jPanel9Layout);
						jPanel3.add(jPanel9);
						{
							sizeDataZ = new JTextField();
							jPanel9.add(sizeDataZ, BorderLayout.CENTER);

						}
					}
				}
			}
			{
				jPanel17 = new JPanel();
				BorderLayout jPanel17Layout = new BorderLayout();
				jPanel17.setLayout(jPanel17Layout);
				jPanel5.add(jPanel17);
				jPanel17.setPreferredSize(new java.awt.Dimension(167, 155));
				jPanel17.setBorder(BorderFactory
						.createTitledBorder(null, "Preview Data Size", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
				{
					jPanel4 = new JPanel();
					jPanel17.add(jPanel4, BorderLayout.NORTH);
					GridLayout jPanel4Layout = new GridLayout(3, 1);
					jPanel4Layout.setColumns(1);
					jPanel4Layout.setHgap(5);
					jPanel4Layout.setVgap(5);
					jPanel4Layout.setRows(3);
					jPanel4.setLayout(jPanel4Layout);
					jPanel4.setBounds(453, 18, 208, 132);
					jPanel4.setSize(257, 100);
					jPanel4.setMaximumSize(new java.awt.Dimension(32767, 100));
					{
						jPanel12 = new JPanel();
						BorderLayout jPanel12Layout = new BorderLayout();
						jPanel12.setLayout(jPanel12Layout);
						jPanel4.add(jPanel12);
						{
							jLabel6 = new JLabel();
							jPanel12.add(jLabel6, BorderLayout.WEST);
							jLabel6.setText("Size X : ");
							jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
							jLabel6.setHorizontalTextPosition(SwingConstants.CENTER);
						}
						{
							sizePreviewX = new JTextField();
							jPanel12.add(sizePreviewX, BorderLayout.CENTER);
						}
					}
					{
						jPanel14 = new JPanel();
						jPanel4.add(jPanel14);
						BorderLayout jPanel14Layout = new BorderLayout();
						jPanel14.setLayout(jPanel14Layout);
						{
							sizePreviewY = new JTextField();
							jPanel14.add(sizePreviewY, BorderLayout.CENTER);
						}
					}
					{
						jPanel13 = new JPanel();
						BorderLayout jPanel13Layout = new BorderLayout();
						jPanel13.setLayout(jPanel13Layout);
						jPanel4.add(jPanel13);
						{
							sizePreviewZ = new JTextField();
							jPanel13.add(sizePreviewZ, BorderLayout.CENTER);
						}
					}
				}
			}
		}
		{
			jPanel18 = new JPanel();
			BorderLayout jPanel18Layout = new BorderLayout();
			jPanel18.setLayout(jPanel18Layout);
			this.add(jPanel18, BorderLayout.CENTER);
			jPanel18.setPreferredSize(new java.awt.Dimension(513, 257));
			{
				fileLocationPanel = new JPanel();
				jPanel18.add(fileLocationPanel, BorderLayout.NORTH);
				BorderLayout fileLocationPanelLayout = new BorderLayout();
				fileLocationPanel.setLayout(fileLocationPanelLayout);
				fileLocationPanel.setBorder(BorderFactory
						.createTitledBorder("File Information"));
			}
			{
				jPanel2 = new JPanel();
				jPanel18.add(jPanel2, BorderLayout.CENTER);
				BorderLayout jPanel2Layout = new BorderLayout();
				jPanel2.setLayout(jPanel2Layout);
				jPanel2.setBounds(19, 171, 642, 205);
				jPanel2.setBorder(BorderFactory
						.createTitledBorder("Information"));
				{
					jScrollPane1 = new JScrollPane(infomation);
					jPanel2.add(jScrollPane1, BorderLayout.CENTER);
				}
			}
		}

		sizeDataZ.setEditable(false);
		{
			jLabel5 = new JLabel();
			jPanel9.add(jLabel5, BorderLayout.WEST);
			jLabel5.setText("Size Z : ");
			jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel5.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		sizeDataY.setEditable(false);
		sizeDataX.setEditable(false);
		{
			jLabel4 = new JLabel();
			jPanel11.add(jLabel4, BorderLayout.WEST);
			jLabel4.setText("Size X : ");
			jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel4.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		sizePreviewZ.setEditable(false);
		{
			jLabel8 = new JLabel();
			jPanel13.add(jLabel8, BorderLayout.WEST);
			jLabel8.setText("Size Z : ");
			jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel8.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		sizePreviewY.setEditable(false);
		{
			jLabel7 = new JLabel();
			jPanel14.add(jLabel7, BorderLayout.WEST);
			jLabel7.setText("Size Y : ");
			jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabel7.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		sizePreviewX.setEditable(false);

		ActionListener change = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{

				int labSize = 50;
				JLabel xLabel = new JLabel("Dim X :");
				JLabel yLabel = new JLabel("Dim Y :");
				JLabel zLabel = new JLabel("Dim Z :");

				xLabel.setPreferredSize(new Dimension(labSize, 0));
				yLabel.setPreferredSize(new Dimension(labSize, 0));
				zLabel.setPreferredSize(new Dimension(labSize, 0));

				xLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				yLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				zLabel.setHorizontalAlignment(SwingConstants.RIGHT);

				NumberDimension xDim = new NumberDimension("m");
				NumberDimension yDim = new NumberDimension("m");
				NumberDimension zDim = new NumberDimension("m");

				xDim.setValue(xSize);
				yDim.setValue(ySize);
				zDim.setValue(zSize);

				JPanel xPan = new JPanel(new BorderLayout());
				JPanel yPan = new JPanel(new BorderLayout());
				JPanel zPan = new JPanel(new BorderLayout());

				xPan.add(xLabel, BorderLayout.WEST);
				yPan.add(yLabel, BorderLayout.WEST);
				zPan.add(zLabel, BorderLayout.WEST);

				xPan.add(xDim, BorderLayout.CENTER);
				yPan.add(yDim, BorderLayout.CENTER);
				zPan.add(zDim, BorderLayout.CENTER);

				JPanel holder = new JPanel(new GridLayout(3, 1, 1, 5));
				holder.add(xPan);
				holder.add(yPan);
				holder.add(zPan);

				JPanel tmp = new JPanel(new BorderLayout());
				tmp.add(holder, BorderLayout.NORTH);

				tmp.setPreferredSize(new Dimension(200, 100));
				if (JOptionPane
						.showConfirmDialog(null, tmp, "Enter New Dimensions", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
				{
					// Prevent Saveing Data
					xSize.blockChangeListner(true);
					ySize.blockChangeListner(true);
					zSize.blockChangeListner(true);

					// Change Values
					xSize.setValue(xDim);
					ySize.setValue(yDim);
					zSize.setValue(zDim);

					// Unblock
					xSize.blockChangeListner(false);
					ySize.blockChangeListner(false);
					zSize.blockChangeListner(false);

					// SaveData
					saveData();

				}
			}
		};

		editSize.addActionListener(change);
		DocumentListener listener = new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent event)
			{
				saveData();
			}

			@Override
			public void insertUpdate(DocumentEvent event)
			{
				saveData();
			}

			@Override
			public void removeUpdate(DocumentEvent event)
			{
				saveData();
			}
		};

		infomation.getDocument().addDocumentListener(listener);

		xSize.addChangeListner(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				saveData();
			}
		});

		ySize.addChangeListner(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				saveData();
			}
		});

		zSize.addChangeListner(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				saveData();
			}
		});
	}

	public void updateMeasurments()
	{

	}

	public void saveData()
	{
		if (dataSet != null && saveData)
		{
			dataSet.sizeDataX = xSize.getValue();
			dataSet.sizeDataY = ySize.getValue();
			dataSet.sizeDataZ = zSize.getValue();

			dataSet.powerX = xSize.getPrefex();
			dataSet.powerY = ySize.getPrefex();
			dataSet.powerZ = zSize.getPrefex();

			dataSet.info = infomation.getText();
		}
	}

	public void setOCTData(NativeDataSet data)
	{
		saveData = false;
		this.dataSet = data;

		// Prevent Saveing Data
		xSize.blockChangeListner(true);
		ySize.blockChangeListner(true);
		zSize.blockChangeListner(true);

		xSize.setValue(data.sizeDataX, true);
		ySize.setValue(data.sizeDataY, true);
		zSize.setValue(data.sizeDataZ, true);

		xSize.setPrefex(data.powerX);
		ySize.setPrefex(data.powerY);
		zSize.setPrefex(data.powerZ);

		// Unblock
		xSize.blockChangeListner(false);
		ySize.blockChangeListner(false);
		zSize.blockChangeListner(false);

		sizeDataX.setText(data.getSizeDataX() + "");
		sizeDataY.setText(data.getSizeDataY() + "");
		sizeDataZ.setText(data.getSizeDataZ() + "");

		sizePreviewX.setText(data.getPreviewSizeX() + "");
		sizePreviewY.setText(data.getPreviewSizeY() + "");
		sizePreviewZ.setText(data.getPreviewSizeZ() + "");

		if (data != null)
		{
			int size = 100;
			if (data instanceof TiffDataSet)
			{
				TiffDataSet dat = (TiffDataSet) (data);
				JPanel holder = new JPanel(new GridLayout(1, 1));

				{
					JLabel label = new JLabel("Tiff File : ");
					label.setHorizontalAlignment(SwingConstants.RIGHT);
					label.setPreferredSize(new Dimension(size, 0));
					JTextField fileField = new JTextField();
					fileField.setEditable(false);

					JPanel fileHolder = new JPanel(new BorderLayout());
					fileHolder.add(label, BorderLayout.WEST);
					fileHolder.add(fileField, BorderLayout.CENTER);

					fileField.setText(dat.tiffFile.toString());
					holder.add(fileHolder);
				}

				fileLocationPanel.removeAll();
				fileLocationPanel.setLayout(new BorderLayout());
				fileLocationPanel.add(holder, BorderLayout.NORTH);
				fileLocationPanel.getTopLevelAncestor().validate();
			} else if (data instanceof NativeDataSet)
			{
				JPanel holder = new JPanel(new GridLayout(2, 1));

				{
					JLabel label = new JLabel("Raw File : ");
					label.setHorizontalAlignment(SwingConstants.RIGHT);
					label.setPreferredSize(new Dimension(size, 0));
					JTextField fileField = new JTextField();
					fileField.setEditable(false);

					JPanel fileHolder = new JPanel(new BorderLayout());
					fileHolder.add(label, BorderLayout.WEST);
					fileHolder.add(fileField, BorderLayout.CENTER);

					fileField.setText(data.rawFile + "");
					holder.add(fileHolder);
				}

				{
					JLabel label = new JLabel("Prv File : ");
					label.setHorizontalAlignment(SwingConstants.RIGHT);
					label.setPreferredSize(new Dimension(size, 0));
					JTextField fileField = new JTextField();
					fileField.setEditable(false);

					JPanel fileHolder = new JPanel(new BorderLayout());
					fileHolder.add(label, BorderLayout.WEST);
					fileHolder.add(fileField, BorderLayout.CENTER);

					fileField.setText(data.previewFile + "");
					holder.add(fileHolder);
				}
				fileLocationPanel.removeAll();
				fileLocationPanel.setLayout(new BorderLayout());
				fileLocationPanel.add(holder, BorderLayout.NORTH);
				if (fileLocationPanel != null
						&& fileLocationPanel.getTopLevelAncestor() != null)
				{
					fileLocationPanel.getTopLevelAncestor().validate();
				}
			}
		}
		infomation.setText("Data : \n");
		infomation.setText(data.info);
		saveData = true;
	}

}
