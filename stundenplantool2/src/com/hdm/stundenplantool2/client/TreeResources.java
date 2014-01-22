package com.hdm.stundenplantool2.client;


import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Style;
import com.google.gwt.user.cellview.client.TreeNode;



public interface TreeResources extends CellTree.Resources {
    @Source("TreeView.css")
    Style cellTreeStyle();
    
    

}


