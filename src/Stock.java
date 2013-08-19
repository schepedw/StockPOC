import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Stock {// Data class.

	private String name;
	private double buyPrice;
	private double sellPrice;
	private int shares;
	private double maxLoss;
	private Date buyTime;

	
	public Stock(String name, double buyPrice, int shares, double maxLoss,
			Date buyTime) {
		this.name = name;
		this.buyPrice = buyPrice;
		this.shares = shares;
		this.maxLoss = maxLoss;
		this.buyTime = buyTime;
	}
	
	/**
	 * sell price - buy price
	 */
	public double getProfit(){
		return this.sellPrice-this.buyPrice;
	}

	protected static void pushPricesToDB(ArrayList<Stock> currentPrices)
			throws ClassNotFoundException, SQLException {
		SQLDBConnection conn = new SQLDBConnection();
		String sql;
		for (Stock stock : currentPrices) {
			if (!isCurrentPrice(stock, conn)) {
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				sql = "Insert into Ticker_Prices (tickerName, price, time) values ('"
						+ stock.getName()
						+ " ',"
						+ stock.getBuyPrice()
						+ ", '"
						+ dateFormat.format(date) + "');";
				conn.executeUpdate(sql);
			}
		}

	}
	
	protected double getCurrentPrice() {
		String sql = "Select Price from " + getName()
				+ "_Prices order by time desc limit 1";
		ResultSet price = new SQLDBConnection().executeQuery(sql);
		double currentPrice = -1;
		try {
			price.next();
			currentPrice = Double.valueOf(price.getString(1));
		} catch (SQLException e) {
			System.err.println("no price entry for" + getName());
			e.printStackTrace();
			System.exit(-1);
		}
		return currentPrice;
	}

	protected static ArrayList<Stock> getStocksFromUser() {
		// in the future, this will bring up a prompt that gets stocks and
		// prices from the user. but for now...
		System.err
				.println("You aren't currently tracking any stocks. Add some to the stocks table in your DB");
		System.exit(-1);
		return null;
	}

	/*
	 * Keeps us from inserting the same data into the table repeatedly
	 */
	private static boolean isCurrentPrice(Stock stock, SQLDBConnection conn)
			throws NumberFormatException, SQLException {
		String sql = "SELECT Price FROM " + stock.getName()
				+ "_Prices ORDER BY time DESC Limit 1;";
		ResultSet currentPrice = conn.executeQuery(sql);
		if (currentPrice.next()
				&& Double.valueOf(currentPrice.getString(1)) == stock
						.getBuyPrice())
			return true;
		return false;
	}
	
	

	/*
	 * Probably a less ugly way to do this...
	 */
	protected boolean isDayTrade() {
		Calendar now = Calendar.getInstance();// set to current time
		Calendar buyDay = Calendar.getInstance();
		buyDay.setTime(this.buyTime);
		if (buyDay.equals(now))
			return false;
		if (buyDay.get(Calendar.MONTH) == now.get(Calendar.MONTH)
				&& buyDay.get(Calendar.DAY_OF_MONTH) == now
						.get(Calendar.DAY_OF_MONTH)
				&& buyDay.get(Calendar.YEAR) == now.get(Calendar.YEAR))
			return true;
		return false;
	}

	public double getMaxLoss() {
		return maxLoss;
	}

	public void setMaxLoss(double maxLoss) {
		this.maxLoss = maxLoss;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Date getBuyTime(){
		return this.buyTime;
	}
	public void setBuyTime(Date time){
		this.buyTime=time;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

}
