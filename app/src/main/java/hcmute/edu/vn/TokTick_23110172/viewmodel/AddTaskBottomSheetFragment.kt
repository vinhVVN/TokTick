package hcmute.edu.vn.TokTick_23110172

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault())
    
    private var selectedListId: Int? = null
    private var fullCategoryList: List<ListCategory> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTaskTitle = view.findViewById<EditText>(R.id.etTaskTitle)
        val tvSelectTime = view.findViewById<TextView>(R.id.tvSelectTime)
        val btnSaveTask = view.findViewById<ImageView>(R.id.btnSaveTask)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)

        tvSelectTime.text = dateFormatter.format(calendar.time)
        tvSelectTime.setOnClickListener {
            showDateTimePicker(tvSelectTime)
        }

        taskViewModel.allCategories.observe(viewLifecycleOwner) { allCategories ->
            fullCategoryList = allCategories
            
            // Hiển thị tất cả: SmartList và Normal List
            // Nhưng nếu chọn SmartList, chúng ta sẽ để listId = null khi lưu (theo logic filter thời gian)
            val displayNames = fullCategoryList.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
            
            // Mặc định chọn Inbox (thường là list đầu tiên không phải SmartList)
            val defaultIndex = fullCategoryList.indexOfFirst { !it.isSmartList }
            if (defaultIndex != -1) {
                spinnerCategory.setSelection(defaultIndex)
                selectedListId = fullCategoryList[defaultIndex].id
            }
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = fullCategoryList[position]
                
                if (selectedCategory.isSmartList) {
                    // Nếu chọn Smart List (Today, Tomorrow, Next 7 Days)
                    // 1. Cập nhật ngày theo Smart List đó
                    updateDateBySmartList(selectedCategory.name, tvSelectTime)
                    // 2. Để listId = null vì nó dùng filter thời gian
                    selectedListId = null
                } else {
                    // Nếu chọn danh mục bình thường
                    selectedListId = selectedCategory.id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                val newTask = Task(
                    title = title,
                    listId = selectedListId, // Có thể null nếu chọn Smart List
                    dueDate = calendar.timeInMillis,
                    dueTime = timeFormatter.format(calendar.time)
                )
                taskViewModel.insert(newTask)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDateBySmartList(name: String, tvSelectTime: TextView) {
        val now = Calendar.getInstance()
        when (name) {
            "Today" -> {
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR))
            }
            "Tomorrow" -> {
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1)
            }
            "Next 7 Days" -> {
                // Mặc định cho Next 7 Days là ngày mai hoặc giữ nguyên ngày hiện tại
                // Tùy logic bạn muốn, ở đây tôi giữ nguyên hoặc set sang ngày mai
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1)
            }
        }
        tvSelectTime.text = dateFormatter.format(calendar.time)
    }

    private fun showDateTimePicker(tvSelectTime: TextView) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        tvSelectTime.text = dateFormatter.format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
