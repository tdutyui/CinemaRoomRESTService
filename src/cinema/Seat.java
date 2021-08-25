package cinema;

public class Seat {
    int row;
    int column;
    int price;

    public Seat() {

    }

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object object) {
        boolean answer = false;

        if (object != null && object instanceof Seat) {
            answer = this.row == ((Seat) object).row &&
                    this.column == ((Seat) object).column &&
                    this.price == ((Seat) object).price;
        }
        return answer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
