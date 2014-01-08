package net.sf.latexdraw.glib.models.impl;

import java.awt.geom.Rectangle2D
import java.util.List
import net.sf.latexdraw.glib.models.GLibUtilities
import net.sf.latexdraw.glib.models.ShapeFactory
import net.sf.latexdraw.glib.models.interfaces.shape.IPoint
import net.sf.latexdraw.glib.models.interfaces.shape.IShape
import net.sf.latexdraw.glib.models.interfaces.shape.ISquare
import net.sf.latexdraw.util.LNumber
import net.sf.latexdraw.glib.models.interfaces.shape.IShape.Position
import net.sf.latexdraw.glib.models.interfaces.prop.ILineArcProp

/**
 * Defines a model of a square.<br>
 * <br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2014 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 * 07/05/2009<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 * @since 3.0
 */
private class LSquare(pos:IPoint, width:Double, uniqueID:Boolean) extends LSquaredShape(pos, width, uniqueID) with ISquare with LineArcProp {
	override def duplicate() : ISquare = {
		super.duplicate match {
			case sq:ISquare => sq
			case _ => null
		}
	}

	override def copy(sh:IShape) {
		super.copy(sh)
		if(sh.isInstanceOf[ILineArcProp])//FIXME in trait
			setLineArc(sh.asInstanceOf[ILineArcProp].getLineArc());
	}
}