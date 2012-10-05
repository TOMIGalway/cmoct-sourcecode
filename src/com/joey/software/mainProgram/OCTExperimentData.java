/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.mainProgram;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import com.joey.software.DataToolkit.NativeDataSet;


public class OCTExperimentData
{
	private String title = "Experiment";

	private NativeDataSet data;

	private ArrayList<OCTViewDataHolder> views = new ArrayList<OCTViewDataHolder>();

	OCTAnalysis owner;

	DefaultMutableTreeNode baseNode = new DefaultMutableTreeNode(this);

	public OCTExperimentData(OCTAnalysis owner, NativeDataSet dataSet, String name)
	{
		setData(dataSet);
		setTitle(name);
		this.owner = owner;
	}

	public void setNodes(DefaultMutableTreeNode top)
	{
		if (top.isNodeChild(baseNode))
		{
			return;
		} else
		{

			reloadTree();
			owner.getModel().insertNodeInto(baseNode, top, top.getChildCount());
		}
	}

	public void reloadTree()
	{
		baseNode.removeAllChildren();
		for (OCTViewDataHolder p : getViews())
		{
			owner.getModel().insertNodeInto(p.getTreeNode(), baseNode, baseNode
					.getChildCount());
		}
	}

	public void addView(OCTViewDataHolder view)
	{
		getViews().add(view);
		owner.getModel().insertNodeInto(view.getTreeNode(), baseNode, baseNode
				.getChildCount());
	}

	public void removeView(OCTViewDataHolder view)
	{
		getViews().remove(view);
		try
		{
			owner.getModel().removeNodeFromParent(view.treeNode);
			if (owner.currentView == view)
			{
				owner.currentExperiment = null;
				owner.currentView = null;
				owner.updateView();
			}
		} catch (Exception e)
		{

		}
	}

	@Override
	public String toString()
	{
		return getTitle();
	}

	public void addView()
	{

		OCTViewDataHolder holder = new OCTViewDataHolder(this,
				"Experiment View : " + getViews().size());
		addView(holder);
	}

	public void setTitle(String title)
	{
		this.title = title;
		try
		{
			owner.getModel().reload(baseNode);
		} catch (Exception e)
		{

		}
	}

	public String getTitle()
	{
		return title;
	}

	public void setData(NativeDataSet data)
	{
		this.data = data;
	}

	public NativeDataSet getData()
	{
		return data;
	}

	public void setViews(ArrayList<OCTViewDataHolder> views)
	{
		this.views = views;
	}

	public ArrayList<OCTViewDataHolder> getViews()
	{
		return views;
	}

	public void renameData()
	{
		JTextField field = new JTextField(title);
		if (JOptionPane
				.showConfirmDialog(null, field, "Enter new Title", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			setTitle(field.getText());
		}

	}

}
