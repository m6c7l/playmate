/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.gui.container.SplitPane;
import de.m6c7l.lib.gui.dialog.InfoDialog;
import de.m6c7l.lib.gui.dialog.TabbedDialog;
import de.m6c7l.lib.gui.tree.BasicTreeNode;
import de.m6c7l.lib.util.crypt.CipherCrypt;
import de.m6c7l.playmate.SysInfo;
import de.m6c7l.playmate.gui.dnd.BasicTreeDragListener;
import de.m6c7l.playmate.gui.dnd.ChartDropListener;
import de.m6c7l.playmate.gui.main.asset.AssetControl;
import de.m6c7l.playmate.gui.main.world.WorldControl;
import de.m6c7l.playmate.gui.side.scenario.Tree;
import de.m6c7l.playmate.gui.window.cep.CEPView;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.gui.window.chart.ChartView;
import de.m6c7l.playmate.gui.window.chart.ChartViewObserver;
import de.m6c7l.playmate.gui.window.tactical.TacticalView;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.Saveable;
import de.m6c7l.playmate.main.World;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;

public class AppUI extends JFrame implements ListDataListener, ItemListener {
	
	private static String TITLE = "";
	private static String AUTHOR = "";
	private static String YEAR = "";

	private JCheckBoxMenuItem menuSound = null;
	private JMenuItem menuNew = null;
	private JMenuItem menuNewEmpty = null;
	private JMenuItem menuEdit = null;
	private JMenuItem menuEditDelete = null;
	private JMenuItem menuNewCreate = null;
	private JMenuItem menuSaveImage = null;
	private JMenuItem menuSaveScenario = null;
	private JMenuItem menuUnits = null;
	private JMenuItem menuAssets = null;
	private JMenuItem menuEnvironment = null;
	private JMenuItem menuLocale = null;
	private JMenuItem menuLocaleDe = null;
	private JMenuItem menuLocaleEn = null;
    private JMenuItem menuAmbience = null;
	private JMenuItem menuExit = null;
	private JMenuItem menuOpen = null;
	private JMenuItem menuInfoAssets = null;
	private JCheckBoxMenuItem menuExercise = null;
	
	private CipherCrypt secure = new CipherCrypt(CipherCrypt.AES);
	private AppModel model = null;
	private AppUI self = null;
	private BasicTreeDragListener drag = null;
	
	private JPanel contentPane = null;
	private SplitPane splitPane = null;
	private JPanel mainPane = null;
	
	private JDesktopPane desktop = null;
	private JInternalFrame desktopChart = null;
	private JInternalFrame desktopCEP = null;
	private JInternalFrame desktopTac = null;
	
	private JPanel selectedPane = null;

	private JTabbedPane tabbedPane = null;
	private JScrollPane treeTab = null;
	private SidePane dispTab = null;
	
	private CEPView cep = null;
	private ChartViewObserver chart = null;
	private ArrayList<ChartView> charts = null;	
	
	private TacticalView tact = null;
	
	private WorldControl worldControl = null;
	private AssetControl assetControl = null;
	
	public AppUI(AppModel model) throws Exception {
		
		super();
		
		SwingUtilities.updateComponentTreeUI(this);
		
		this.self = this;
		
		secure.setKey(this.getClass().getPackage().getName());
		
		final URL urlStart = ClassLoader.getSystemResource("resources/image/start.png");
				
		TITLE = "Playmate \u00b7 Practical Submarine Simulator";
		AUTHOR = "Manfred Constapel";
		YEAR = "2010-2018";

		this.setTitle(TITLE);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension scr = TOOLBOX.getScreenSize();
		
		double gs = (-1+Math.sqrt(5))/2;
		double sq = Math.sqrt(2);
		double f = Math.pow(gs*sq,2);
		
		setSize((int)(scr.width*f),(int)(scr.height*f));
		setLocationRelativeTo(null);

		this.model = model;

		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuNew = new JMenu("New");
		menuFile.add(menuNew);

		menuNewEmpty = new JMenuItem("Empty scenario");
		menuNew.add(menuNewEmpty);

		menuNewCreate = new JMenuItem("Create scenario");
		menuNew.add(menuNewCreate);
		menuNewCreate.setEnabled(false);

		menuOpen = new JMenuItem("Open ...");
		menuFile.add(menuOpen);
		menuOpen.setEnabled(false);
		
		JMenu menuSave = new JMenu("Save");

		menuSaveImage = new JMenuItem("Image as ...");
		menuSave.add(menuSaveImage);
		
		menuSaveScenario = new JMenuItem("Scenario as ...");
		menuSave.add(menuSaveScenario);
		
		menuFile.add(menuSave);

		menuExit = new JMenuItem("Exit");
		menuFile.add(menuExit);

		menuEdit = new JMenu("Edit");

		menuEditDelete = new JMenuItem("Remove");
		menuEdit.add(menuEditDelete);
		menuEditDelete.setEnabled(false);

		menuBar.add(menuEdit);

		JMenu menuSettings = new JMenu("Settings");
		menuBar.add(menuSettings);

		menuExercise = new JCheckBoxMenuItem("Exercise");
		menuSettings.add(menuExercise);

		menuEnvironment = new JMenuItem("Environment");
        menuSettings.add(menuEnvironment);
        menuEnvironment.setEnabled(false);
        
        JMenu menuDisplay = new JMenu("Display");
		
        menuUnits = new JMenuItem("Units");
        menuDisplay.add(menuUnits);
		menuUnits.setEnabled(false);

		menuAssets = new JMenuItem("Assets");
		menuDisplay.add(menuAssets);
		menuAssets.setEnabled(false);
		
		menuSettings.add(menuDisplay);
		
		menuLocale = new JMenu("Language");
		menuSettings.add(menuLocale);
		
		menuLocaleDe = new JRadioButtonMenuItem("German");
		menuLocale.add(menuLocaleDe);
		
		menuLocaleEn = new JRadioButtonMenuItem("Englisch");
		menuLocale.add(menuLocaleEn);
		
		ButtonGroup btnGrp = new ButtonGroup();
		btnGrp.add(menuLocaleDe);
		btnGrp.add(menuLocaleEn);
		
		menuLocaleDe.setEnabled(false);
        menuLocaleEn.setSelected(true);

        menuAmbience = new JMenu("Ambience");
        menuSettings.add(menuAmbience);

		menuSound = new JCheckBoxMenuItem("Sound");
		menuSound.setEnabled(model.getSound()!=null);
		menuAmbience.add(menuSound);
		
		JMenu menuInfo = new JMenu("Info");
		menuBar.add(menuInfo);

		this.setJMenuBar(menuBar);

		final URL urlInfo = ClassLoader.getSystemResource("resources/image/info.png");
		final InfoDialog info = new InfoDialog(self,"About");
		info.setImage(new ImageIcon(urlInfo));
		info.setText(

		        "Copyright (c) " + YEAR + ", " + AUTHOR + "\n\n" +

                "Playmate is a submarine simulator designed for training in target motion analysis "+
                "exclusively using the contact evaluation plot (CEP). The CEP is a time-bearing diagram "+
                "on a submarine showing the trace of sound emissions tracked by sonar.\n\n" +
                
		        "Permission is hereby granted, free of charge, to any person obtaining a copy "+
		        "of this software and associated documentation files (the \"Software\"), to deal "+
		        "in the Software without restriction, including without limitation the rights "+
		        "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell "+
		        "copies of the Software, and to permit persons to whom the Software is "+
		        "furnished to do so, subject to the following conditions: \n\n"+

		        "The above copyright notice and this permission notice shall be included in all "+
		        "copies or substantial portions of the Software. \n\n"+

		        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR "+
		        "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, "+
		        "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE "+
		        "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER "+
		        "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, "+
		        "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE "+
		        "SOFTWARE."
					
		);
		
		JMenuItem menuInfoAbout = new JMenuItem("About ...");
		menuInfoAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				info.setVisible(true);
			}
		});
		menuInfo.add(menuInfoAbout);

		final SysInfo.Filesystem sysfs = new SysInfo.Filesystem();		
		final TabbedDialog dd = new TabbedDialog(self,"System");			
		JMenuItem menuInfoSystem = new JMenuItem("System ...");
		menuInfoSystem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dd.addTab(new String[] {"Virtual Machine",},	"Properties", 	new SysInfo.Properties());
				dd.addTab(new String[] {"Virtual Machine",}, 	"Memory", 		new SysInfo.Memory());
				dd.addTab(new String[] {}, 						"File System", 		sysfs);
				dd.setVisible(true);	
			}
		});
		menuInfo.add(menuInfoSystem);
		
		final TabbedDialog ee = new TabbedDialog(self,"Assets");
		menuInfoAssets = new JMenuItem("Assets ...");
		menuInfoAssets.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				World world = getModel().getWorld();
				ee.clear();
				for (int i=0; i<world.getAssetCount(); i++) {
					Asset asset = world.getAsset(i);
					String id = asset.getID()==null ? asset.getClass().getSimpleName() : asset.getID().toString();
					ee.addTab(new String[] {id},	"Track",		asset.getTrack());
					ee.addTab(new String[] {id},	"Orientation",	asset.getOrientation());
					ee.addTab(new String[] {id},	"Route",		asset.getRoute());
				}	
				ee.setVisible(true);	
			}
		});
		menuInfo.add(menuInfoAssets);

		contentPane = new JPanel();		
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		worldControl = new WorldControl(model.getWorld()) {
			public void invalidate() {
				if (!isDragging()) super.invalidate();
			}
		};
		contentPane.add(worldControl, BorderLayout.SOUTH);
				
		mainPane = new JPanel();
		contentPane.add(mainPane, BorderLayout.CENTER);
		mainPane.setLayout(new BorderLayout());
		
		assetControl = new AssetControl(model.getWorld(),worldControl.getPreferredSize().height);
		mainPane.add(assetControl, BorderLayout.SOUTH);
		
		splitPane = new SplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation((int)(getWidth()*((2+Math.sqrt(5)) / 25)));
		splitPane.setDividerSize(5);
		
		mainPane.add(splitPane, BorderLayout.CENTER);
		
		treeTab = new JScrollPane();
		dispTab = new SidePane(model.getWorld());
		
		tabbedPane = new JTabbedPane();		
		tabbedPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		tabbedPane.add("Scenario", treeTab);
        tabbedPane.add("Assets", dispTab);
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setEnabledAt(1,false);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex()==1) {
					selectChart(chart);
				} else if (tabbedPane.getSelectedIndex()==0) {
					for (int i=0; i<getTree().getModel().getNodeChart().getChildCount(); i++) {
						TreeNode node = getTree().getModel().getNodeChart().getChildAt(i);
						if (((BasicTreeNode)node).getUserObject()==(chart.getChart())) {
							TreePath path = new TreePath(node);
							getTree().setSelectionPath(path);
							select((BasicTreeNode)node);
							break;
						}
					}
				}
			}
		});
		
		splitPane.setLeftComponent(tabbedPane);
		
		this.treeTab.getViewport().setView(new Tree(model.getTree()));
		
		ChartView[] c = AppModel.createChartViews(model);
		charts = new ArrayList<ChartView>();
		for (int i=0; i<c.length; i++) {
			charts.add(c[i]);
			model.getTree().addPanel(c[i]);
			new ChartDropListener(c[i],model);
		}
		getTree().expand(model.getTree().getNodeChart(),true);
		
		getTree().addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent ev) {

				BasicTreeNode node = (BasicTreeNode)getTree().getLastSelectedPathComponent();
				
				if (node != null) {
					select(node);					
				}
				
			}
		});
		
		drag = new BasicTreeDragListener(this);

		chart = new ChartViewObserver(getModel().getWorld(),charts);
		cep = new CEPView(model.getWorld());
		tact = new TacticalView(model);
		
		model.getTree().setChartsEnabled(true);
		
		dispTab.getChooserSelectAsset().addFocusListener(cep.getCEP());
		dispTab.getChooserHookAsset().addFocusListener(cep.getCEP());
		
		getMenuItemSaveImage().setEnabled(false);
		getMenuItemSaveScenario().setEnabled(false);
		getMenuItemInfoAssets().setEnabled(false);
		
		getMenuItemExercise().setSelected(false);
		getMenuItemExercise().setEnabled(false);

		model.getWorld().addItemListener(this);
		model.getWorld().addListDataListener(this);
		
		model.getWorld().addItemListener(tact.getTactical());		
		
		desktop = new Desktop(urlStart);
		splitPane.setRightComponent(desktop);
		
		desktopChart = new JInternalFrame("Chart",true,false,true,true);
		
		desktopChart.setSize(
				(int)(scr.width*f*f*f),
				(int)(scr.height*f*f*0.9));

		desktopChart.setLocation(
				(int)(scr.width*0.02),
				(int)(scr.height*0.02));
		
		desktopChart.getContentPane().setLayout(new BorderLayout());
		desktopChart.setBorder(new CompoundBorder(new EmptyBorder(3,3,3,3),desktopChart.getBorder()));
		desktopChart.setMinimumSize(new Dimension(desktopChart.getSize().width/2,desktopChart.getSize().height/2));
		desktopChart.setVisible(true);
		desktopChart.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {
				if (desktopCEP.isIcon() && desktopTac.isIcon()) getMenuItemSaveImage().setEnabled(false);
			}
			public void internalFrameDeiconified(InternalFrameEvent e) {
				getMenuItemSaveImage().setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {
				for (int i=0; i<desktopChart.getContentPane().getComponentCount(); i++) {
					if (desktopChart.getContentPane().getComponent(i) instanceof JPanel) {
						selectedPane = (JPanel)desktopChart.getContentPane().getComponent(i);
						return;
					}
				}
				selectedPane = null;
			}
		});
		desktop.add(desktopChart);

		desktopCEP = new JInternalFrame("CEP",true,false,true,true);
		desktopCEP.setSize(
				(int)(scr.width*f*f*f),
				(int)(scr.height*f*f*0.9));
		
		desktopCEP.setLocation(
				(int)(scr.width*0.05),
				(int)(scr.height*0.05));
		
		desktopCEP.getContentPane().setLayout(new BorderLayout());
		desktopCEP.setBorder(new CompoundBorder(new EmptyBorder(3,3,3,3),desktopCEP.getBorder()));
		desktopCEP.setMinimumSize(new Dimension(desktopCEP.getSize().width/2,desktopCEP.getSize().height/2));
		desktopCEP.setVisible(true);
		desktopCEP.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {
                if (desktopChart.isIcon() && desktopTac.isIcon()) getMenuItemSaveImage().setEnabled(false);
			}
			public void internalFrameDeiconified(InternalFrameEvent e) {
				getMenuItemSaveImage().setEnabled(true);
			}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {
				for (int i=0; i<desktopCEP.getContentPane().getComponentCount(); i++) {
					if (desktopCEP.getContentPane().getComponent(i) instanceof JPanel) {
						selectedPane = (JPanel)desktopCEP.getContentPane().getComponent(i);
						return;
					}
				}
				selectedPane = null;
			}
		});
		desktop.add(desktopCEP);
		show(desktopCEP,cep);
		
        desktopTac = new JInternalFrame("Tactical",true,false,true,true);
        desktopTac.setSize(
                (int)(scr.width*f*f*f),
                (int)(scr.height*f*f*0.9));
        
        desktopTac.setLocation(
                (int)(scr.width*0.08),
                (int)(scr.height*0.08));
        
        desktopTac.getContentPane().setLayout(new BorderLayout());
        desktopTac.setBorder(new CompoundBorder(new EmptyBorder(3,3,3,3),desktopTac.getBorder()));
        desktopTac.setMinimumSize(new Dimension(desktopTac.getSize().width/2,desktopTac.getSize().height/2));
        desktopTac.setVisible(true);
        desktopTac.addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameOpened(InternalFrameEvent e) {}
            public void internalFrameIconified(InternalFrameEvent e) {
                if (desktopChart.isIcon() && desktopCEP.isIcon()) getMenuItemSaveImage().setEnabled(false);
            }
            public void internalFrameDeiconified(InternalFrameEvent e) {
                getMenuItemSaveImage().setEnabled(true);
            }
            public void internalFrameDeactivated(InternalFrameEvent e) {}
            public void internalFrameClosing(InternalFrameEvent e) {}
            public void internalFrameClosed(InternalFrameEvent e) {}
            public void internalFrameActivated(InternalFrameEvent e) {
                for (int i=0; i<desktopTac.getContentPane().getComponentCount(); i++) {
                    if (desktopTac.getContentPane().getComponent(i) instanceof JPanel) {
                        selectedPane = (JPanel)desktopTac.getContentPane().getComponent(i);
                        return;
                    }
                }
                selectedPane = null;
            }
        });
        desktop.add(desktopTac);
        show(desktopTac,tact);
		
		if (getTree().getModel().getNodeChart().getChildCount()>0) {
			TreeNode node = getTree().getModel().getNodeChart().getChildAt(0);
			TreePath path = new TreePath(node);
			getTree().setSelectionPath(path);
			select((BasicTreeNode)node);
		}
		
		desktopChart.setIcon(false);
        desktopCEP.setIcon(false);
        desktopTac.setIcon(false);
	
	}
	
	public void free() throws Exception {

		this.model.getWorld().removeListDataListener(this);
		
		self = null;
		model = null;

		drag.free();
		drag = null;
		
		this.setJMenuBar(null);
		this.setRootPane(null);

		splitPane.removeAll();
		mainPane.remove(splitPane);
		splitPane = null;
		
		tabbedPane.removeAll();
		tabbedPane = null;
		
		assetControl.free();		
		mainPane.remove(assetControl);
		assetControl = null;
		
		worldControl.free();
		contentPane.remove(worldControl);
		worldControl = null;
		
		dispTab.free();
		dispTab = null;
		
		cep.free();
		cep = null;
		
		chart = null;
		
		for (int i=0; i<charts.size(); i++) charts.get(i).free();
		charts.clear();

		desktopCEP.removeAll();
		desktopChart.removeAll();
		desktop.removeAll();

		desktopCEP = null;
		desktopChart = null;
		desktop = null;

		selectedPane = null;
		
		getTree().free();
		this.treeTab.getViewport().setView(null);
		
	}
	
	public boolean isDragging() {
		return (drag!=null) ? drag.isDragging() : false;
	}
	
	/*
	 * menu
	 */
	
	public JMenuItem getMenuItemNewEmpty() {
		return this.menuNewEmpty;
	}
	
	public JMenuItem getMenuItemNewCreate() {
		return this.menuNewCreate;
	}
	
	public JMenuItem getMenuItemSaveImage() {
		return this.menuSaveImage;
	}
	
	public JMenuItem getMenuItemSaveScenario() {
		return this.menuSaveScenario;
	}
	
	public JMenuItem getMenuItemExit() {
		return this.menuExit;
	}
	
	public JCheckBoxMenuItem getMenuItemExercise() {
	    return this.menuExercise;
	}

	public JMenuItem getMenuItemUnits() {
		return this.menuUnits;
	}

	public JMenuItem getMenuItemAssets() {
		return this.menuAssets;
	}
	
	public JMenuItem getMenuItemEnvironment() {
		return this.menuEnvironment;
	}
	
	public JMenuItem getMenuItemInfoAssets() {
		return this.menuInfoAssets;
	}
	
	public JMenuItem getMenuItemSound() {
		return this.menuSound;
	}

	public JMenuItem getMenuEditDelete() { 
	    return this.menuEditDelete;
	}

	/*
	 * view
	 */

	public Tree getTree() {
		return (Tree)(treeTab.getViewport().getView());
	}

	public JPanel getViewSelected() {
		return this.selectedPane;
	}
	
	public JTabbedPane getTabbed() {
		return tabbedPane;
	}
	
	public AppModel getModel() {
		return this.model;
	}

	/*
	 *  item listener
	 */

	public void itemStateChanged(ItemEvent e) {
		getMenuEditDelete().setEnabled((model.getWorld().getSelected() != null) &&  (!model.getWorld().isExercising()));
		getMenuItemExercise().setEnabled((model.getWorld().getObserver() != null));
		getMenuItemInfoAssets().setEnabled((model.getWorld().getAssetCount()>0));
	}

	/*
	 *  list listener
	 */

	public void contentsChanged(ListDataEvent e) {
		listStateChanged();
	}

	public void intervalAdded(ListDataEvent e) {
		listStateChanged();
	}

	public void intervalRemoved(ListDataEvent e) {
		listStateChanged();
	}

	private void listStateChanged() {

		getMenuItemInfoAssets().setEnabled((model.getWorld().getAssetCount()>0));

		if ((model.getWorld().getAssetCount()>0) && (model.getWorld().getObserver()!=null)) {
			tabbedPane.setEnabledAt(1,true);
		}
		
		if (model.getWorld().getObserver()!=null) {
			if (model.getWorld().getAssetCount()>1) {
				getMenuItemExercise().setEnabled(true);		
			}
		}

	}
	
	private void select(BasicTreeNode node) {

		if (getModel().getTree().isPanel(node)) {
			
			JPanel nodePanel = getModel().getTree().getPanel(node);

			if (nodePanel instanceof ChartView) {
				selectChart((ChartView)nodePanel);				
			}
				
		}
		
	}	
	
	private void selectChart(ChartViewObserver select) {

		if (desktopChart.isSelected() || desktop.getComponentZOrder(desktopChart)==0) {
			selectedPane = select;
		}

		show(desktopChart,select);
		
	}
	
	private void selectChart(ChartView select) {

		Chart chart = select.getChart();

		getModel().addChart(chart);

		if (desktopChart.isSelected() || desktop.getComponentZOrder(desktopChart)==0) {
			selectedPane = select;
		}

		show(desktopChart,select);
		
	}
	
	private void show(final JInternalFrame master, JPanel slave) {
		
		getMenuItemSaveImage().setEnabled(false);
		
		master.getContentPane().removeAll();
		
		if (slave!=null) {
			
			master.getContentPane().add(slave, BorderLayout.CENTER);
			slave.setVisible(true);			

			if (slave instanceof Saveable) {	
				getMenuItemSaveImage().setEnabled(true);					
			}

		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				master.revalidate();
				master.repaint();	
			}
		});
		
	}
		
}
