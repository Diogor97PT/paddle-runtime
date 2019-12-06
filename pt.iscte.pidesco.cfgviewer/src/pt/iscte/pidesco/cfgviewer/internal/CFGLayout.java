package pt.iscte.pidesco.cfgviewer.internal;

import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import pt.iscte.paddle.model.cfg.IBranchNode;
import pt.iscte.paddle.model.cfg.INode;

public class CFGLayout extends AbstractLayoutAlgorithm {
	
	/*private static final double DELTA = 10;
	private static final double HSPACING = 2;*/
	
	private static final int SPACING = 30;
	private INode lastNode;

	public CFGLayout(int styles) {
		super(styles);
	}
	
	/*
	 * 
	 * O código aparenta desenhar primeiro os nós da guarda verdadeira (alternative) e so quando acaba os true é que desenhas os false
	 * Tentar usar isto para formatar o desenho?
	 * 
	 */
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		
		int currentY = 50;
		int currentX = 50;
		
		for(InternalNode in : entitiesToLayout) {
			INode node = (INode)((GraphNode)in.getLayoutEntity().getGraphData()).getData();	//Aqui consigo aceder ao conteudo do node
			
			/*if(!(node instanceof IBranchNode)) {
				in.setLocation(currentX, currentY);
				
//				currentWidth += node.getWidthInLayout() + SPACING;
				
			} else {
				System.out.println("NOT");
			}*/
			
			in.setLocation(currentX, currentY);
			
			currentY += in.getHeightInLayout() + SPACING;
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
