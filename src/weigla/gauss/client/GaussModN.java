package weigla.gauss.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.collect.Sets;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class GaussModN implements EntryPoint, ClickHandler {
    private Button btnSubmit;
    private TextBoxGrid inputTable;
    private Button btnIncM;
    private Button btnDecM;

    private Button btnIncN;
    private Button btnDecN;

    private TextBox txtModul;
    private FlexTable solution;

    private VerticalPanel opsPanel;

    public void onModuleLoad() {
	RootPanel.get("inputArea");

	solution = new FlexTable();
	opsPanel = new VerticalPanel();

	RootPanel.get("outputArea").add(solution);
	RootPanel.get("outputArea").add(opsPanel);

	txtModul = new TextBox();
	txtModul.setText("29");
	txtModul.setAlignment(TextAlignment.RIGHT);
	txtModul.setWidth("5ex");

	inputTable = new TextBoxGrid(4, 3);
	btnSubmit = new Button("Solve");
	btnIncM = new Button(">");
	btnDecM = new Button("<");

	btnIncN = new Button("v");
	btnDecN = new Button("^");

	btnIncM.setSize("3em", "3em");
	btnDecM.setSize("3em", "3em");
	btnIncN.setSize("3em", "3em");
	btnDecN.setSize("3em", "3em");

	btnIncM.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		inputTable.increaseM();
	    }
	});
	btnDecM.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		inputTable.decreaseM();
	    }
	});

	btnIncN.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		inputTable.increaseN();
	    }
	});
	btnDecN.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		inputTable.decreaseN();
	    }
	});

	btnSubmit.addClickHandler(this);

	HorizontalPanel hp = new HorizontalPanel();
	HorizontalPanel hpR = new HorizontalPanel();
	VerticalPanel vpSide = new VerticalPanel();
	VerticalPanel vp = new VerticalPanel();
	vpSide.add(btnIncM);
	vpSide.add(btnDecM);

	vp.getElement().setAttribute("align", "center");

	hpR.add(btnIncN);
	hpR.add(btnDecN);

	hpR.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	hpR.add(new HTMLPanel(
		"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Modul <em>n</em> = :"));
	hpR.add(txtModul);

	hp.add(inputTable);
	hp.add(vpSide);
	vp.add(hp);
	vp.add(hpR);
	vp.add(btnSubmit);
	RootPanel.get("inputArea").add(vp);
    }

    @Override
    public void onClick(ClickEvent event) {

	final int m = inputTable.getM();
	final int n = inputTable.getN();
	int modul = Integer.parseInt(txtModul.getText());
	int[][] matrix = inputTable.asMatrix(modul);
	Gauss.gauss(matrix, m, n, modul);

	solution.removeAllRows();
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < m; j++) {
		solution.setText(i, j, "" + matrix[i][j]);
	    }
	}

	opsPanel.clear();
	for (String s : Gauss.ops) {
	    opsPanel.add(new HTMLPanel(s));
	}
    }
}

class TextBoxGrid extends FlexTable {
    TextBox[][] boxes = new TextBox[0][0];
    private int n = 0;
    private int m = 0;

    public TextBoxGrid(int m, int n) {
	setSize(m, n);
    }

    public void increaseM() {
	setSize(m + 1, n);
    }

    public void decreaseM() {
	if (m - 1 >= n) {
	    setSize(m - 1, n - 1);
	} else {
	    setSize(m - 1, n);
	}
    }

    public void increaseN() {
	if (m >= n + 1) {
	    setSize(m + 1, n + 1);
	} else {
	    setSize(m, n + 1);
	}
    }

    public void decreaseN() {
	setSize(m, n - 1);
    }

    public TextBox factory() {
	TextBox t = new TextBox();
	t.setWidth("5ex");
	t.setText("0");
	t.setAlignment(TextAlignment.RIGHT);
	return t;
    }

    public void setSize(int m, int n) {
	int oldm = this.m, oldn = this.n;

	this.m = m;
	this.n = n;

	TextBox[][] nbox = new TextBox[n][m];
	int N = Math.min(n, oldn);
	int M = Math.min(m, oldm);

	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < m; j++) {
		if (i < N && j < M)
		    nbox[i][j] = boxes[i][j];
		else
		    nbox[i][j] = factory();
	    }
	}

	boxes = nbox;

	removeAllRows();

	for (int i = 0; i < n; i++) {
	    insertRow(0);
	    for (int j = 0; j < m; j++) {
		addCell(0);
	    }
	}

	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < m; j++) {
		if (j < n) {
		    boxes[i][j].setStyleName("matrix");
		    boxes[i][j].setTabIndex(i * n + j);
		} else {
		    boxes[i][j].setStyleName("vectors");
		    boxes[i][j].setTabIndex(i * n + j + +1 + n * n);
		}

		setWidget(i, j, boxes[i][j]);
	    }
	}
    }

    public int[][] asMatrix(int modul) {
	int[][] d = new int[n][m];
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < m; j++) {
		d[i][j] = Util.mod(Integer.parseInt(boxes[i][j].getText()),
			modul);
	    }
	}
	return d;
    }

    public int getN() {
	return n;
    }

    public int getM() {
	return m;
    }

}
