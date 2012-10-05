package com.joey.software.framesToolkit;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JScrollPane;

/**
 * A JTable that draws a zebra striped background.
 */
public class ZebraJTable extends javax.swing.JTable
{
	private java.awt.Color rowColors[] = new java.awt.Color[2];

	private boolean drawStripes = false;

	public static void main(String input[])
	{
		Vector<Vector<String>> items = new Vector<Vector<String>>();

		for (int i = 0; i < 1000; i++)
		{
			Vector<String> row = new Vector<String>();
			row.add("R" + i);
			row.add("R" + i);
			row.add("R" + i);
			items.add(row);
		}
		Vector<String> names = new Vector<String>();
		names.add("1");
		names.add("2");
		names.add("3");

		ZebraJTable table = new ZebraJTable(items, names);
		table.setBackground(Color.darkGray);
		table.setForeground(Color.white);
		table.setSelectionBackground(Color.yellow);
		table.setSelectionForeground(Color.black);
		JScrollPane scrollList = new JScrollPane(table);
		FrameFactroy.getFrame(scrollList);
	}

	public ZebraJTable()
	{
	}

	public ZebraJTable(int numRows, int numColumns)
	{
		super(numRows, numColumns);
	}

	public ZebraJTable(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
	}

	public ZebraJTable(javax.swing.table.TableModel dataModel)
	{
		super(dataModel);
	}

	public ZebraJTable(javax.swing.table.TableModel dataModel, javax.swing.table.TableColumnModel columnModel)
	{
		super(dataModel, columnModel);
	}

	public ZebraJTable(javax.swing.table.TableModel dataModel, javax.swing.table.TableColumnModel columnModel, javax.swing.ListSelectionModel selectionModel)
	{
		super(dataModel, columnModel, selectionModel);
	}

	public ZebraJTable(java.util.Vector<?> rowData, java.util.Vector<?> columnNames)
	{
		super(rowData, columnNames);
	}

	/** Add stripes between cells and behind non-opaque cells. */
	@Override
	public void paintComponent(java.awt.Graphics g)
	{
		if (!(drawStripes = isOpaque()))
		{
			super.paintComponent(g);
			return;
		}

		// Paint zebra background stripes
		updateZebraColors();
		final java.awt.Insets insets = getInsets();
		final int w = getWidth() - insets.left - insets.right;
		final int h = getHeight() - insets.top - insets.bottom;
		final int x = insets.left;
		int y = insets.top;
		int rowHeight = 16; // A default for empty tables
		final int nItems = getRowCount();
		for (int i = 0; i < nItems; i++, y += rowHeight)
		{
			rowHeight = getRowHeight(i);
			g.setColor(rowColors[i & 1]);
			g.fillRect(x, y, w, rowHeight);
		}
		// Use last row height for remainder of table area
		final int nRows = nItems + (insets.top + h - y) / rowHeight;
		for (int i = nItems; i < nRows; i++, y += rowHeight)
		{
			g.setColor(rowColors[i & 1]);
			g.fillRect(x, y, w, rowHeight);
		}
		final int remainder = insets.top + h - y;
		if (remainder > 0)
		{
			g.setColor(rowColors[nRows & 1]);
			g.fillRect(x, y, w, remainder);
		}

		// Paint component
		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	/** Add background stripes behind rendered cells. */
	@Override
	public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col)
	{
		final java.awt.Component c = super.prepareRenderer(renderer, row, col);
		if (drawStripes && !isCellSelected(row, col))
			c.setBackground(rowColors[row & 1]);
		return c;
	}

	/** Add background stripes behind edited cells. */
	@Override
	public java.awt.Component prepareEditor(javax.swing.table.TableCellEditor editor, int row, int col)
	{
		final java.awt.Component c = super.prepareEditor(editor, row, col);
		if (drawStripes && !isCellSelected(row, col))
			c.setBackground(rowColors[row & 1]);
		return c;
	}

	/** Force the table to fill the viewport's height. */
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		final java.awt.Component p = getParent();
		if (!(p instanceof javax.swing.JViewport))
			return false;
		return ((javax.swing.JViewport) p).getHeight() > getPreferredSize().height;
	}

	/** Compute zebra background stripe colors. */
	private void updateZebraColors()
	{
		if ((rowColors[0] = getBackground()) == null)
		{
			rowColors[0] = rowColors[1] = java.awt.Color.white;
			return;
		}
		final java.awt.Color sel = getSelectionBackground();
		if (sel == null)
		{
			rowColors[1] = rowColors[0];
			return;
		}
		final float[] bgHSB = java.awt.Color
				.RGBtoHSB(rowColors[0].getRed(), rowColors[0].getGreen(), rowColors[0]
						.getBlue(), null);
		final float[] selHSB = java.awt.Color.RGBtoHSB(sel.getRed(), sel
				.getGreen(), sel.getBlue(), null);
		rowColors[1] = java.awt.Color
				.getHSBColor((selHSB[1] == 0.0 || selHSB[2] == 0.0) ? bgHSB[0]
						: selHSB[0], 0.1f * selHSB[1] + 0.9f * bgHSB[1], bgHSB[2]
						+ ((bgHSB[2] < 0.5f) ? 0.05f : -0.05f));
	}
}