import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.with_runn.R
import com.with_runn.data.model.FollowResponse
import com.with_runn.data.model.Notice
import com.with_runn.databinding.ItemNoticeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoticeAdapter(
    private val callbacks: Callbacks,
    private val scope: CoroutineScope
) : ListAdapter<Notice, NoticeAdapter.NoticeViewHolder>(DIFF) {

    interface Callbacks {
        suspend fun onFollowClick(notice: Notice): FollowResponse
        fun onItemClick(notice: Notice)
        fun onProfileClick(actorId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoticeViewHolder(
            ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            callbacks,
            scope
        )

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) =
        holder.bind(getItem(position))

    class NoticeViewHolder(
        private val binding: ItemNoticeBinding,
        private val callbacks: Callbacks,
        private val scope: CoroutineScope
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) = with(binding) {
            // 1) 텍스트 바인딩
            alertText.text = notice.message

            // 2) 타입별 아이콘 선택 (null이면 GONE)
            val iconRes = when (notice.noticeType) {
                "LIKE"  -> R.drawable.ic_heart_hollow
                "SCRAP" -> R.drawable.ic_bookmark_hollow
                else    -> null
            }
            if (iconRes != null) {
                alertTypeImg.visibility = View.VISIBLE
                alertTypeImg.setImageResource(iconRes)
            } else {
                alertTypeImg.visibility = View.GONE
            }

            // 3) 클릭 리스너
            userImg.setOnClickListener { callbacks.onProfileClick(notice.actorId) }
            root.setOnClickListener { callbacks.onItemClick(notice) }

            // 4) 팔로우 버튼: 콜백의 FollowResponse 반환에 따라 정적 변경
            followBtn.setOnClickListener {
                followBtn.isEnabled = false
                scope.launch {
                    val resp = callbacks.onFollowClick(notice)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(itemView.context, resp.message, Toast.LENGTH_SHORT).show()
                        if (resp.success) {
                            followBtn.text = "팔로잉"
                            followBtn.background = AppCompatResources.getDrawable(
                                itemView.context, R.drawable.bg_button_inactive
                            )
                            followBtn.setTextColor(
                                ContextCompat.getColor(itemView.context, R.color.green_700)
                            )
                        }
                        followBtn.isEnabled = true
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem == newItem
        }
    }
}
