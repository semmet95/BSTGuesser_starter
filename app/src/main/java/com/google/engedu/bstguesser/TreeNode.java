/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.bstguesser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class TreeNode {
    private static final int SIZE = 60;
    private static final int MARGIN = 20;
    int value, height;
    protected TreeNode left, right;
    private boolean showValue;
    private int x, y;
    private int color = Color.rgb(150, 150, 250);
    static int treeHeight=1;

    private static BinarySearchTree caller;
    private ArrayList<TreeNode> pathToNodeInserted=new ArrayList<>();
    private boolean isParentRoot=false;

    public TreeNode(int value, BinarySearchTree ob) {
        this.value = value;
        this.height = 0;
        showValue = false;
        left = null;
        right = null;

        caller=ob;
    }

    public TreeNode(int value) {
        this.value = value;
        this.height = 0;
        showValue = false;
        left = null;
        right = null;
    }

    public void insert(int valueToInsert) {
        TreeNode temp=this;
        pathToNodeInserted.clear();
        while(true) {
            pathToNodeInserted.add(0, temp);
            if(valueToInsert<temp.value) {
                if(temp.left==null) {
                    temp.left=new TreeNode(valueToInsert);
                    pathToNodeInserted.add(0, temp.left);
                    break;
                }
                else
                    temp=temp.left;
            } else {
                if(temp.right==null) {
                    temp.right=new TreeNode(valueToInsert);
                    pathToNodeInserted.add(0, temp.right);
                    break;
                }
                else
                    temp=temp.right;
            }
        }

        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        caller.setheights();
        int parentIndex=-1;
        if(BinarySearchTree.root.height>1)
            parentIndex=getParentIndex();
        if(parentIndex!=-1) {
            TreeNode parent=pathToNodeInserted.get(parentIndex);
            TreeNode kid=pathToNodeInserted.get(parentIndex-1);
            TreeNode kiddo=pathToNodeInserted.get(parentIndex-2);
            //the four rotation cases

            //left
            if(parent.right==kid&&kid.right==kiddo) {
                TreeNode subtree=kid.left;
                kid.left=parent;
                parent.right=subtree;
                if(!isParentRoot) {
                    TreeNode grandparent=pathToNodeInserted.get(parentIndex+1);
                    if(grandparent.left==parent)
                        grandparent.left=kid;
                    else
                        grandparent.right=kid;
                } else {
                    BinarySearchTree.root=kid;
                }
            }

            //right
            else if(parent.left==kid&&kid.left==kiddo) {
                TreeNode subtree=kid.right;
                kid.right=parent;
                parent.left=subtree;
                if(!isParentRoot) {
                    TreeNode grandparent=pathToNodeInserted.get(parentIndex+1);
                    if(grandparent.left==parent)
                        grandparent.left=kid;
                    else
                        grandparent.right=kid;
                } else {
                    BinarySearchTree.root=kid;
                }
            }

            //left-right
            else if(parent.left==kid&&kid.right==kiddo) {
                parent.left=kiddo;
                TreeNode subtree=kiddo.left;
                kiddo.left=kid;
                kid.right=subtree;
                subtree=kiddo.right;
                kiddo.right=parent;
                parent.left=subtree;
                if(!isParentRoot) {
                    TreeNode grandparent=pathToNodeInserted.get(parentIndex+1);
                    if(grandparent.left==parent)
                        grandparent.left=kiddo;
                    else
                        grandparent.right=kiddo;
                } else {
                    BinarySearchTree.root=kiddo;
                }
            }

            //right-left
            else if(parent.right==kid&&kid.left==kiddo) {
                parent.right=kiddo;
                TreeNode subtree=kiddo.right;
                kiddo.right=kid;
                kid.left=subtree;
                subtree=kiddo.left;
                kiddo.left=parent;
                parent.right=subtree;
                if(!isParentRoot) {
                    TreeNode grandparent=pathToNodeInserted.get(parentIndex+1);
                    if(grandparent.left==parent)
                        grandparent.left=kiddo;
                    else
                        grandparent.right=kiddo;
                } else {
                    BinarySearchTree.root=kiddo;
                }
            }
        }
    }

    public int getValue() {
        return value;
    }

    public void positionSelf(int x0, int x1, int y) {
        this.y = y;
        x = (x0 + x1) / 2;

        if(left != null) {
            left.positionSelf(x0, right == null ? x1 - 2 * MARGIN : x, y + SIZE + MARGIN);
        }
        if (right != null) {
            right.positionSelf(left == null ? x0 + 2 * MARGIN : x, x1, y + SIZE + MARGIN);
        }
    }

    public void draw(Canvas c) {
        treeHeight+=30;
        //Log.e("TreeNodeDraw :", "trying to draw the lines");
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.GRAY);
        if (left != null)
            c.drawLine(x, y + SIZE/2, left.x, left.y + SIZE/2, linePaint);
        if (right != null)
            c.drawLine(x, y + SIZE/2, right.x, right.y + SIZE/2, linePaint);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        c.drawRect(x-SIZE/2, y, x+SIZE/2, y+SIZE, fillPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(SIZE * 2/3);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(showValue ? String.valueOf(value) : "?", x, y + SIZE * 3/4, paint);

        if (height > 0) {
            Paint heightPaint = new Paint();
            heightPaint.setColor(Color.MAGENTA);
            heightPaint.setTextSize(SIZE * 2 / 3);
            heightPaint.setTextAlign(Paint.Align.LEFT);
            c.drawText(String.valueOf(height), x + SIZE / 2 + 10, y + SIZE * 3 / 4, heightPaint);
        }

        if (left != null)
            left.draw(c);
        if (right != null)
            right.draw(c);
    }

    public int click(float clickX, float clickY, int target) {
        int hit = -1;
        if (Math.abs(x - clickX) <= (SIZE / 2) && y <= clickY && clickY <= y + SIZE) {
            if (!showValue) {
                if (target != value) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
            }
            showValue = true;
            hit = value;
        }
        if (left != null && hit == -1)
            hit = left.click(clickX, clickY, target);
        if (right != null && hit == -1)
            hit = right.click(clickX, clickY, target);
        return hit;
    }

    public void invalidate() {
        color = Color.CYAN;
        showValue = true;
    }

    private int getParentIndex() {
        for(int i=0;i<pathToNodeInserted.size();i++) {
            TreeNode x = pathToNodeInserted.get(i);
            int leftHeight, rightHeight;
            if(x.left!=null)
                leftHeight=x.left.height;
            else
                leftHeight=-1;
            if(x.right!=null)
                rightHeight=x.right.height;
            else
                rightHeight=-1;
            if (diff(leftHeight, rightHeight) > 1) {
                isParentRoot = (i == pathToNodeInserted.size() - 1);
                return i;
            }
        }
        return -1;
    }

    private int diff(int x, int y) {
        return (x>y)?x-y:y-x;
    }
}
