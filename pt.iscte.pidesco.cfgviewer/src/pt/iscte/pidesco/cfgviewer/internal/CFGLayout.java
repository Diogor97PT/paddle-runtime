package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import pt.iscte.paddle.model.cfg.IBranchNode;

public class CFGLayout extends AbstractLayoutAlgorithm {
	
	private static final int SPACING = 30;
	private static final int startY = 20;
	private static final int startX = 50;

	public CFGLayout(int styles) {
		super(styles);
	}
	
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		
		/*for(InternalNode n : entitiesToLayout) {
			n.setLocation(startX, currentY);
			currentY += n.getLayoutEntity().getHeightInLayout() + SPACING;
		}*/
		
		for(int i = 0; i < entitiesToLayout.length; i++) {
			entitiesToLayout[i].setLocation(startX, startY + ((entitiesToLayout[i].getLayoutEntity().getHeightInLayout() + SPACING) * i));
		}
		
		for(InternalNode in : entitiesToLayout) {
			GraphNode gn = (GraphNode) in.getLayoutEntity().getGraphData();

			if(gn.getData() instanceof IBranchNode) {
				IBranchNode node = (IBranchNode) gn.getData();

				for(Object obj : gn.getSourceConnections()) {
					GraphConnection gc = (GraphConnection)obj;
					LayoutEntity le = gc.getDestination().getLayoutEntity();;
					if(gc.getDestination().getData().equals(node.getAlternative()) && !(gc.getDestination().getLayoutEntity().getYInLayout() < gc.getSource().getLayoutEntity().getYInLayout())) {
						le.setLocationInLayout(gc.getSource().getLayoutEntity().getXInLayout() + le.getWidthInLayout() + SPACING, le.getYInLayout());
					} else if (gc.getDestination().getData().equals(node.getNext()) && !(gc.getDestination().getLayoutEntity().getYInLayout() < gc.getSource().getLayoutEntity().getYInLayout())){
						le.setLocationInLayout(gc.getSource().getLayoutEntity().getXInLayout(), le.getYInLayout());
					}
				}
			}
		}
	}
	

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		return true;
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		return 0;
	}

	@Override
	protected int getCurrentLayoutStep() {
		return 0;
	}

}
