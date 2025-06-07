import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenote.R
import com.example.voicenote.home.Memo

class MemoAdapter(
    private val originalList: List<Memo>,
    private val onItemClick: (Memo) -> Unit // ✅ 클릭 시 실행할 콜백 함수 추가
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private var filteredList: List<Memo> = originalList.toList()

    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textSummary: TextView = itemView.findViewById(R.id.textSummary)
        val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = filteredList[position]
        holder.textTitle.text = memo.title
        holder.textSummary.text = memo.summary
        holder.textDateTime.text = memo.dateTime

        // ✅ 클릭 시 onItemClick 콜백 호출
        holder.itemView.setOnClickListener {
            onItemClick(memo)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
