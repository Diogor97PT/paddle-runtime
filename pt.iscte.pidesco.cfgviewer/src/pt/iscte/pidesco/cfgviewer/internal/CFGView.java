package pt.iscte.pidesco.cfgviewer.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;

import pt.iscte.paddle.model.cfg.IBranchNode;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.model.cfg.INode;
import pt.iscte.paddle.model.cfg.IStatementNode;
import pt.iscte.pidesco.extensibility.PidescoView;

public class CFGView implements PidescoView {
	
	/*
	 * Criar layout customizado (ver horizontalshift mas fazer vertical)
	 * 
	 * Marcar caminho apenas
	 * Marcar caminho e caixas
	 * Marcar apenas caixas
	 * 
	 * Escrever texto na ligação entre 2 pares
	 * 
	 * Listener para cliques em certos nós
	 *  
	 * 
	 * */

	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		GraphViewer gv = new GraphViewer(viewArea, SWT.BORDER);
		
		gv.setContentProvider(new GraphNodeContentProvider());
		gv.setLabelProvider(new GraphLabelContentProvider());
		//gv.addSelectionChangedListener(listener);  //Pode vir a ser útil
		
		IControlFlowGraph cfg = CFG_Creator.create_cfg();
		gv.setInput(cfg.getNodes());
		
//		gv.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), false);
//		gv.setLayoutAlgorithm(new CompositeLayoutAlgorithm(new LayoutAlgorithm[] {new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING)}));
//		gv.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
//		gv.setLayoutAlgorithm(new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		
		gv.setLayoutAlgorithm(new CFGLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		gv.applyLayout();
	}
	
	class GraphNodeContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {

		@Override
		public Object[] getConnectedTo(Object entity) {
			List<INode> connections = new LinkedList<>();
			connections.add(((INode) entity).getNext());
			
			if(entity instanceof IBranchNode) 
				connections.add(((IBranchNode) entity).getAlternative());
			
			return connections.toArray();
		}
	}
	
	class GraphLabelContentProvider extends LabelProvider implements IFigureProvider, IConnectionStyleProvider {

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
		public Color getColor(Object rel) {
			return ColorConstants.red;
		}

		@Override
		public Color getHighlightColor(Object rel) {
			return ColorConstants.blue;
		}

		@Override
		public int getLineWidth(Object rel) {
			return 1;
		}

		@Override
		public IFigure getTooltip(Object entity) {
			return new CFGFigure("Tooltip");
		}

		@Override
		public IFigure getFigure(Object element) {
			if (element instanceof IStatementNode) {
				IStatementNode node = (IStatementNode) element;
				return new CFGFigure(node.getElement().toString());
			} else if (element instanceof IBranchNode) {
				IBranchNode node = (IBranchNode) element;
				return new CFGBranchFigure(node.getElement().toString(), "This is a Branch");
			} else {
				INode node = (INode) element;
				if(node.isEntry()) 
					return new CFGFigure("Entry");
				else 
					return new CFGFigure("Exit");
				
			}
		}
		
		@Override
		public String getText(Object element) {
			if(element instanceof EntityConnectionData) {
				EntityConnectionData ecd = (EntityConnectionData) element;
				if(ecd.source instanceof IBranchNode) {
					IBranchNode node = (IBranchNode)ecd.source;
					if(node.getAlternative().equals(ecd.dest))
						return "True";
					else
						return "False";
				}
			}
			
			return "";
		}
		
		/*@Override
		public Image getImage(Object element) {		//Possivelmente remover
			return super.getImage(element);
		}*/
		
		
	}
	

}
