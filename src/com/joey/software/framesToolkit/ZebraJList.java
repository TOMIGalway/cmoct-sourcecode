package com.joey.software.framesToolkit;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JScrollPane;

/**
 * A JList that supports a zebra stripe background.
 * http://nadeausoftware.com/articles
 * /2008/01/java_tip_how_add_zebra_background_stripes_jlist
 */
public class ZebraJList extends javax.swing.JList
{
	private java.awt.Color rowColors[] = new java.awt.Color[2];

	private boolean drawStripes = false;

	public static void main(String input[])
	{
		Vector<String> items = new Vector<String>();
		items.add("hello");

		for (int i = 0; i < 10000; i++)
		{
			items.add("Value : " + i);
		}
		ZebraJList list = new ZebraJList(items);
		list.setPrototypeCellValue(items.get(0));
		// list.setBackground( Color.darkGray );
		// list.setForeground( Color.white );
		list.setSelectionBackground(Color.yellow);
		list.setSelectionForeground(Color.black);
		JScrollPane scrollList = new JScrollPane(list);
		FrameFactroy.getFrame(scrollList);
	}

	public ZebraJList()
	{
	}

	public ZebraJList(javax.swing.ListModel dataModel)
	{
		super(dataModel);
	}

	public ZebraJList(Object[] listData)
	{
		super(listData);
	}

	public ZebraJList(java.util.Vector<?> listData)
	{
		super(listData);
	}

	/** Add zebra stripes to the background. */
	@Override
	public void paintComponent(java.awt.Graphics g)
	{
		drawStripes = (getLayoutOrientation() == VERTICAL) && isOpaque();
		if (!drawStripes)
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
		int nRows = 0;
		int startRow = 0;
		int rowHeight = getFixedCellHeight();
		if (rowHeight > 0)
			nRows = h / rowHeight;
		else
		{
			// Paint non-uniform height rows first
			final int nItems = getModel().getSize();
			rowHeight = 17; // A default for empty lists
			for (int i = 0; i < nItems; i++, y += rowHeight)
			{
				rowHeight = getCellBounds(i, i).height;
				g.setColor(rowColors[i & 1]);
				g.fillRect(x, y, w, rowHeight);
			}
			// Use last row height for remainder of list area
			nRows = nItems + (insets.top + h - y) / rowHeight;
			startRow = nItems;
		}
		for (int i = startRow; i < nRows; i++, y += rowHeight)
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

	/** Wrap a cell renderer to add zebra stripes behind list cells. */
	private class RendererWrapper implements javax.swing.ListCellRenderer
	{
		public javax.swing.ListCellRenderer ren = null;

		@Override
		public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			final java.awt.Component c = ren
					.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!isSelected && drawStripes)
				c.setBackground(rowColors[index & 1]);
			return c;
		}
	}

	private RendererWrapper wrapper = null;

	/** Return the wrapped cell renderer. */
	@Override
	public javax.swing.ListCellRenderer getCellRenderer()
	{
		final javax.swing.ListCellRenderer ren = super.getCellRenderer();
		if (ren == null)
			return null;
		if (wrapper == null)
			wrapper = new RendererWrapper();
		wrapper.ren = ren;
		return wrapper;
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