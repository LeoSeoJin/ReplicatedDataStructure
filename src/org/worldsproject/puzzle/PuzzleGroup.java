package org.worldsproject.puzzle;

import java.util.HashSet;

import com.example.puzzle.GameActivity;
import com.example.puzzle.network.wifi.pack.Global;
import com.example.puzzle.network.wifi.pack.MessageService;
import com.example.puzzle.sharedMemory.AbstractSharedRegister;

public class PuzzleGroup extends AbstractSharedRegister{
	private static int idSource = 0;
	private final int serial = ++idSource;
	
	private HashSet<Piece> group = new HashSet<Piece>();

	private MessageService msgService = new MessageService(Global.APP, Global.DEVICENAME, Global.IP);
	
	public PuzzleGroup(Piece p) {
		group.add(p);
	}

	public void addPiece(Piece piece) {
		group.add(piece);
		piece.setGroup(this);
	}

	public void translate(int x, int y) {
		for (Piece p : group) {
			int xD = p.getX() - x;
			int yD = p.getY() - y;

			p.setX(xD);
			p.setY(yD);
		}
		write("writepiece",this);
	}

	public void addGroup(PuzzleGroup oldGroup) {
		for (Piece p : oldGroup.getGroup()) {
			this.addPiece(p);
		}
	}

	public HashSet<Piece> getGroup() {
		return group;
	}

	public boolean sameGroup(Piece a, Piece b) {
		return a.getGroup().getSerial() == b.getGroup().getSerial();
	}

	public int getSerial() {
		return serial;
	}

	@Override
	public Object read() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void write(String type, PuzzleGroup g) {
		if (type.equals("writepiece")) {
			int[] p_array = new int[g.getGroup().size()];
			int[] x_array = new int[g.getGroup().size()];
			int[] y_array = new int[g.getGroup().size()];
			int i = 0;
			for (Piece p: g.getGroup()) {
				p_array[i] = p.getSerial();
				x_array[i] = p.getX();
				y_array[i] = p.getY();
				i++;
			}
			msgService.sendMsg(msgService.structMessage("writepiece", p_array, x_array, y_array));
		}
		if (type.equals("updategroup")) {
			int[] group_pieces = new int[g.getGroup().size()];
			int i = 0;
			for (Piece p: g.getGroup()) {
				group_pieces[i++] = p.getSerial();
			}
			msgService.sendMsg(msgService.structMessage("updategroup", group_pieces));
		}
	}
}
