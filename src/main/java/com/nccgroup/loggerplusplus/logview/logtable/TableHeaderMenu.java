package com.nccgroup.loggerplusplus.logview.logtable;

import com.nccgroup.loggerplusplus.logview.logtable.LogTable;
import com.nccgroup.loggerplusplus.logview.logtable.LogTableColumn;
import com.nccgroup.loggerplusplus.util.MoreHelp;
import com.nccgroup.loggerplusplus.util.Globals;
import com.nccgroup.loggerplusplus.LoggerPlusPlus;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class TableHeaderMenu extends JPopupMenu{

	private final LogTableController logTableController;
	private final LogTable logTable;
	private final LogTableColumn columnObj;

	public TableHeaderMenu(LogTableController logTableController, LogTableColumn columnObj)
	{
		this.logTableController = logTableController;
		this.logTable = logTableController.getLogTable();
		this.columnObj=columnObj;

	}

	public void showMenu(MouseEvent e){
		boolean isRegex=columnObj.isRegEx();

		JPopupMenu menu = new JPopupMenu("Popup");
		JMenuItem item = new JMenuItem(columnObj.getVisibleName() + " (" + columnObj.getIdentifier().getFullLabel() + ")");

		item.setEnabled(false);
		menu.add(item);
		menu.addSeparator();

		if(isRegex){
			JMenu submenu = new JMenu("Regex");

			item = new JMenuItem("Edit");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							String newValue = MoreHelp.showPlainInputMessage("Regular expression for the \"" + columnObj.getVisibleName()+
									"\" column", "Edit Regex", columnObj.getRegExData().getRegExString());
							// Save it only if it is different! no need to refresh the columns
							if(!newValue.equals(columnObj.getRegExData().getRegExString())){
								// a mew RegEx string has been provided - we need to ensure that it is a valid regular expression to prevent confusion!
								try {
						            Pattern.compile(newValue);
						            columnObj.getRegExData().setRegExString(newValue);
						        } catch (PatternSyntaxException exception) {
						            LoggerPlusPlus.callbacks.printError("Regular expression was invalid. It cannot be saved.");
						            MoreHelp.showWarningMessage("The provided regular expression string was invalid. It cannot be saved.");
						        }
							}
						}
					});
				}
			});

			submenu.add(item);		

			item = new JCheckBoxMenuItem("Case sensitive");
			item.setSelected(columnObj.getRegExData().isRegExCaseSensitive());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					columnObj.getRegExData().setRegExCaseSensitive(!columnObj.getRegExData().isRegExCaseSensitive());
				}
			});

			submenu.add(item);

			menu.add(submenu);


		}

		item = new JMenuItem("Rename");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newValue = MoreHelp.showPlainInputMessage("Rename the \"" + columnObj.getDefaultVisibleName()+
						"\" column", "Rename column name", columnObj.getVisibleName());
				if(newValue.isEmpty()){
					newValue = columnObj.getDefaultVisibleName();
				}
				// Save it only if it is different! no need to refresh the columns
				if(!newValue.equals(columnObj.getDefaultVisibleName())){
					columnObj.setVisibleName(newValue);
					logTableController.getLogTableColumnModel().saveLayout();
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("Hide");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logTable.getColumnModel().toggleHidden(columnObj);
			}
		});
		menu.add(item);

		JMenu subMenuVisibleCols = new JMenu("Visible columns");
		item = new JMenuItem("Make all visible");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Enumeration<TableColumn> columnEnumeration = logTable.getColumnModel().getColumns();
				while (columnEnumeration.hasMoreElements()) {
					LogTableColumn logTableColumn = (LogTableColumn) columnEnumeration.nextElement();
					if(!logTableColumn.isVisible()){
						logTable.getColumnModel().toggleHidden(logTableColumn);
					}
				}
				logTableController.getLogTableColumnModel().saveLayout();
			}
		});
		subMenuVisibleCols.add(item);

		Enumeration<LogTableColumn> columnEnumeration = logTable.getColumnModel().getAllColumns();
		while (columnEnumeration.hasMoreElements()) {
			LogTableColumn logTableColumn = (LogTableColumn) columnEnumeration.nextElement();
			JMenuItem visibleItem = new JCheckBoxMenuItem(logTableColumn.getVisibleName());
			visibleItem.setSelected(logTableColumn.isVisible());
			visibleItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logTable.getColumnModel().toggleHidden(logTableColumn);
				}
			});
			subMenuVisibleCols.add(visibleItem);
		}

		menu.add(subMenuVisibleCols);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}



}


