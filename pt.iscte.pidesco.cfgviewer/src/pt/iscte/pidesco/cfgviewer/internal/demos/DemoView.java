package pt.iscte.pidesco.cfgviewer.internal.demos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import pt.iscte.pidesco.extensibility.PidescoView;

public class DemoView implements PidescoView {
	
	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		GraphViewer viewer = new GraphViewer(viewArea, SWT.BORDER);
		viewer.setContentProvider(new ZestNodeContentProvider());
		viewer.setLabelProvider(new ZestFigureProvider());
		List<String> model = new ArrayList<String>();
		model.add("A");
		model.add("B");
		model.add("C");
		viewer.setInput(model);
		viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		viewer.applyLayout();
	}
	
	class ZestNodeContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {
	    @Override
	    public Object[] getConnectedTo(Object entity) {
	    	if(entity.equals("A"))
	    		return new Object[] {"B","C"};
	    	return new Object[0]; 
	    }
	}
	
	class ZestFigureProvider extends LabelProvider implements IFigureProvider, IConnectionStyleProvider, ISelfStyleProvider {
		@Override
		public IFigure getFigure(Object element) {
			return new DemoFigure(element.toString());
		}
		
		@Override
		public String getText(Object element) {
			return "qq coisa";
		}

		@Override
		public int getConnectionStyle(Object rel) {
			return ZestStyles.CONNECTIONS_DASH;
		}

		@Override
		public Color getColor(Object rel) {
			return ColorConstants.red;
		}

		@Override
		public Color getHighlightColor(Object rel) {
			return null;
		}

		@Override
		public int getLineWidth(Object rel) {
			return 1;
		}

		@Override
		public IFigure getTooltip(Object entity) {
			return null;
		}

		@Override
		public void selfStyleConnection(Object element, GraphConnection connection) {
			PolylineConnection connectionFig = (PolylineConnection) connection.getConnectionFigure();
			PolygonDecoration decoration = new PolygonDecoration();
			decoration.setScale(20, 10);
			decoration.setLineWidth(2);
			decoration.setOpaque(true);
			decoration.setBackgroundColor(ColorConstants.white);
			connectionFig.setTargetDecoration(decoration);
		}

		@Override
		public void selfStyleNode(Object element, GraphNode node) {
			
		}		
	}
}
