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
package com.joey.software.VolumeToolkit;

/*
 *	@(#)JPanelChoiceAxis.java 1.2 99/09/15 13:44:14
 *
 * Copyright (c) 1996-1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class JPanelChoiceAxis extends AttrComponent
{
	JPanel block = new JPanel();

	JPanel left = new JPanel();

	JPanel right = new JPanel();

	JPanel bottom = new JPanel();

	ButtonGroup bg = new ButtonGroup();

	JRadioButton[] items;

	JPanelChoiceAxis(ActionListener al, JPanel panel, ChoiceAttr attr)
	{
		super(attr);
		block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		JLabel label = new JLabel(attr.getLabel());
		block.add(label);
		items = new JRadioButton[attr.valueNames.length];
		for (int i = 0; i < attr.valueNames.length; i++)
		{
			items[i] = new JRadioButton(attr.valueLabels[i]);
			items[i].setActionCommand(attr.valueNames[i]);
			items[i].setName(attr.getName());
			items[i].addActionListener(al);
			if (i == 0)
			{
				block.add(items[i]);
			} else if ((i % 2) == 1)
			{
				left.add(items[i]);
			} else
			{
				right.add(items[i]);
			}
			bg.add(items[i]);
		}
		bottom.add(left);
		bottom.add(right);
		bottom.setAlignmentX(0.0f);
		block.add(bottom);
		panel.add(block);
		update();
	}

	@Override
	public void update()
	{
		items[((ChoiceAttr) attr).getValue()].setSelected(true);
	}
}
