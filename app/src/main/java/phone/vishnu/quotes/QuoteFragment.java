package phone.vishnu.quotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuoteFragment extends Fragment {


    public QuoteFragment() {
    }

    public static final QuoteFragment newInstance(String quote, String author) {

        QuoteFragment fragment = new QuoteFragment();

        Bundle bundle = new Bundle();
        bundle.putString("quote", quote);
        bundle.putString("author", author);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);

        TextView quoteText = quoteView.findViewById(R.id.quoteTextView);
        TextView authorText = quoteView.findViewById(R.id.authorTextView);

        String quote = getArguments().getString("quote");
        String author = getArguments().getString("author");
        quoteText.setText(quote);
        authorText.setText("-" + author);

        return quoteView;
    }


}
