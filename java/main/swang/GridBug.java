package main.swang;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;

/**
 * Use gridwidth and gridheight to make a component span extra columns/rows
 * Use gridx and gridy to control position; also addX() and addY()
 * Use fill + weightx and weighty to control expansion; also setFill() and weightXY()
 * Finally use insets to control padding; also setInsets()
 */
public class GridBug extends GridBagConstraints {

  public GridBagLayout gbl=new GridBagLayout();
  public Container container;

  public GridBug(Container c) {
    this.container=c;
    this.gridx=0;
    this.gridy=0;
    container.setLayout(gbl);
  }

  public Container getContainer() {
    return container;
  }

  public GridBug add(Component c) {
    gbl.setConstraints(c, this);
    container.add(c);
    return this;
  }

  public GridBug setInsets(int i) {
    return setInsets(i, i, i, i);
  }
  public GridBug setInsets(int top, int right, int bottom, int left) {
    insets.top=top;
    insets.right=right;
    insets.bottom=bottom;
    insets.left=left;
    return this;
  }
  public GridBug insets(int i) {
    return setInsets(i, i, i, i);
  }
  public GridBug insets(int top, int right, int bottom, int left) {
    return setInsets(top, right, bottom, left);
  }
  public GridBug insetTop(int inset) {insets.top=inset; return this;}
  public GridBug insetRight(int inset) {insets.right=inset; return this;}
  public GridBug insetBottom(int inset) {insets.bottom=inset; return this;}
  public GridBug insetLeft(int inset) {insets.left=inset; return this;}

  public GridBug addXY() {
    gridx++;
    gridy++;
    return this;
  }

  public GridBug setX(int gridx){
    this.gridx=gridx;
    return this;
  }
  public GridBug x(int gridx){
    this.gridx=gridx;
    return this;
  }
  public GridBug addX(Component... cs) {
    for (Component c: cs)
      addX(c);
    return this;
  }
  public GridBug addX(Component c) {
    add(c);
    gridx++;
    return this;
  }
  public GridBug addX() {
    gridx++;
    return this;
  }
  public GridBug addXadd(Component c) {
    gridx++;
    add(c);
    return this;
  }

  public GridBug setY(int gridy){
    this.gridy=gridy;
    return this;
  }
  public GridBug y(int gridy){
    this.gridy=gridy;
    return this;
  }
  public GridBug addY(Component... cs) {
    for (Component c: cs)
      addY(c);
    return this;
  }
  public GridBug addY(Component c) {
    add(c);
    gridy++;
    return this;
  }
  public GridBug addY() {
    gridy++;
    return this;
  }
  public GridBug addYadd(Component c) {
    gridy++;
    add(c);
    return this;
  }

  public GridBug weightXY(double x, double y) {
    weightx=x; weighty=y; return this;
  }
  public GridBug weightXY(double xy) {
    return weightXY(xy, xy);
  }
  public GridBug weightX(double x) {
    weightx=x; return this;
  }
  public GridBug weightY(double y) {
    weighty=y; return this;
  }
  public GridBug gridXY(int x, int y) {
    gridx=x; gridy=y; return this;
  }
  public GridBug gridXY(int xy) {
    return gridXY(xy, xy);
  }
  public GridBug gridX(int x) {
    this.gridx=x; return this;
  }
  public GridBug gridY(int y) {
    this.gridy=y; return this;
  }
  public GridBug setFill(int fill) {
    this.fill=fill;
    return this;
  }
  public GridBug fill(int fill) {
    this.fill=fill; return this;
  }
  public GridBug anchor(int anchor) {
    this.anchor=anchor; return this;
  }
  public GridBug gridWidth(int gridWidth) {
    this.gridwidth=gridWidth; return this;
  }
  public GridBug gridHeight(int gridHeight) {
    this.gridheight=gridHeight; return this;
  }

}