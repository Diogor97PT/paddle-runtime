package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import pt.iscte.paddle.model.cfg.IBranchNode;

public class CFGLayout extends AbstractLayoutAlgorithm {
	
	/*private static final double DELTA = 10;
	private static final double HSPACING = 2;*/
	
	private static final int SPACING = 30;

	public CFGLayout(int styles) {
		super(styles);
	}
	
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		
		for(InternalRelationship r : relationshipsToConsider) {
//			System.out.println("R: " + r.toString());
			System.out.println("InternalRelationship GraphData: " + r.getGraphData());
			
		}
		
		for(InternalNode node : entitiesToLayout) {
			System.out.println("InternalNode graphdata: " + node.getLayoutInformation());
			
			LayoutEntity e = node.getLayoutEntity();
//			System.out.println("fsdfiudsfdnbfiu: " + e.getGraphData());
			GraphNode g = (GraphNode)e.getGraphData();
			
			
			if(g.getData() instanceof IBranchNode) {		//Aqui consigo aceder ao conteudo do node
				System.out.println("Branch");
			} else {
				System.out.println("not branch");
			}
		}
		
		int currentHeight = 50;
		int currentWidth = 50;
		
//		boolean isAfterBranch = false;
		for(InternalNode node : entitiesToLayout) {
//			if(!isAfterBranch) {
				node.setLocation(currentWidth, currentHeight);
				currentHeight += node.getHeightInLayout() + SPACING;
//				currentWidth += node.getWidthInLayout() + SPACING;
				
//			}
			
			
		}
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getCurrentLayoutStep() {
		// TODO Auto-generated method stub
		return 0;
	}

}
