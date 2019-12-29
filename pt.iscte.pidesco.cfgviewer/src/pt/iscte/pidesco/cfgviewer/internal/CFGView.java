package pt.iscte.pidesco.cfgviewer.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;

import pt.iscte.paddle.model.cfg.IBranchNode;
import pt.iscte.paddle.model.cfg.INode;
import pt.iscte.paddle.model.cfg.IStatementNode;
import pt.iscte.pidesco.cfgviewer.ext.IColorScheme;

public class CFGView {
	
	/*
	 * Marcar caminho apenas
	 * Marcar caminho e caixas
	 * Marcar apenas caixas
	 * 
	 * Escrever texto na ligação entre 2 pares
	 * 
	 * Listener para cliques em certos nós
	 * 
	 * */
	
	private GraphViewer gv;
	private IColorScheme ics;
	
	public CFGView(Composite viewArea, IColorScheme ics) {
		gv = new GraphViewer(viewArea, SWT.BORDER);
		this.ics = ics;

		gv.setContentProvider(new GraphNodeContentProvider());
		gv.setLabelProvider(new GraphLabelContentProvider());
		//gv.addSelectionChangedListener(listener);  //Pode vir a ser útil
		
		gv.setLayoutAlgorithm(new CFGLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		gv.applyLayout();
	}
	
	public void setInput(Object input) {
		gv.setInput(input);
	}
	
	private class GraphNodeContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {

		@Override
		public Object[] getConnectedTo(Object entity) {
			List<INode> connections = new LinkedList<>();
			connections.add(((INode) entity).getNext());
			
			if(entity instanceof IBranchNode) 
				connections.add(((IBranchNode) entity).getAlternative());
			
			return connections.toArray();
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return super.getElements(inputElement);
		}
	}
	
	private class GraphLabelContentProvider extends LabelProvider implements IFigureProvider, IConnectionStyleProvider {
		
		/*fazer colorscheme para este caso (tipo de ligação tracejado, normal, etc)*/
		@Override
		public int getConnectionStyle(Object rel) {
			if(rel instanceof EntityConnectionData) {
				EntityConnectionData ecd = (EntityConnectionData) rel;
				if(ecd.source instanceof IBranchNode && ((IBranchNode)ecd.source).getAlternative().equals(ecd.dest)) {
					return ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED;
				}
			}
			return ZestStyles.CONNECTIONS_DIRECTED;
		}

		@Override
		public Color getColor(Object rel) {		//Color of the connections
			if(rel instanceof EntityConnectionData) {
				EntityConnectionData ecd = (EntityConnectionData) rel;
				return ics.getConnectionColor((INode)ecd.source, (INode)ecd.dest);
			}
			
			return ColorConstants.red;
		}

		@Override
		public Color getHighlightColor(Object rel) {	//Color of the Highlight
//			return ColorConstants.blue;
			if(rel instanceof EntityConnectionData) {
				EntityConnectionData ecd = (EntityConnectionData) rel;
				return ics.getHighlightColor((INode)ecd.source, (INode)ecd.dest);
			}
			return ColorConstants.blue;
		}

		@Override
		public int getLineWidth(Object rel) {
			return 2;
		}

		@Override
		public IFigure getTooltip(Object entity) {
//			return new CFGFigure("Tooltip");
			return null;
		}

		@Override
		public IFigure getFigure(Object element) {
			if (element instanceof IStatementNode) {
				IStatementNode node = (IStatementNode) element;
				return new CFGFigure(node.getElement().toString(), ics.getNodeColor(node), ics.getNodeBorderColor(node));
			} else if (element instanceof IBranchNode) {
				IBranchNode node = (IBranchNode) element;
				return new CFGBranchFigure(node.getElement().toString(), ics.getNodeColor(node), ics.getNodeBorderColor(node));
			} else {
				INode node = (INode) element;
				if(node.isEntry()) 
					return new CFGExitFigure(ics.getStartNodeColor());
				else 
					return new CFGExitFigure(ics.getEndNodeColor());
				
			}
		}
		
		@Override
		public String getText(Object element) {
			if(element instanceof EntityConnectionData) {
				EntityConnectionData ecd = (EntityConnectionData) element;
				return ics.getConnectionText((INode)ecd.source, (INode)ecd.dest);
//				if(ecd.source instanceof IBranchNode) {
//					IBranchNode node = (IBranchNode)ecd.source;
//					if(node.getAlternative().equals(ecd.dest))
//						return "True";
//					else
//						return "False";
//				}
			}
			
			return "";
		}
	}
	

}
