package com.example.suprisequizapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.suprisequizapplication.R
import com.example.suprisequizapplication.model.Question

class QuestionAdapter(
    private val questionList: MutableList<Question>,
    private val onOptionTextEntered: (Int, Int, String) -> Unit,
    private val onQuestionTextEntered: (Int, String) -> Unit,
    private val onOptionDeleted: (Int, Int) -> Unit,
    private val onDeleteQuestion: (Int) -> Unit,
    private val onCopyQuestion: (Int,Question) -> Unit,
    private val onAddQuestion: () -> Unit,
    private val onOptionAddButtonClicked: (Int) -> Unit,
    private val onAnswerKeySelected: (Int, Int) -> Unit,
    private val onSetAnswerKeyClicked: (Int) -> Unit,
    private val onImageQuestionClicked: (Int) -> Unit

) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.question_layout,
                    parent,
                    false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionList[position]
        holder.uiEtQuestion.hint = "Question ${position+1}"
        holder.uiEtQuestion.setText(question.text)
        Log.d("QuestionAdapter", "onBindViewHolder: $questionList")

        if (question.options?.isNotEmpty() == true) {
            holder.uiRvOptions.apply {
                adapter = OptionsAdapter(
                    optionsList = question.options ?: listOf(),
                    onOptionTextEntered = { optionPosition, optionText ->
                        onOptionTextEntered(position,
                            optionPosition,
                            optionText)
                        //notifyDataSetChanged() -> error: page refreshes on every new letter typed
                        // so cursor shifts it position from question editText to title editText
                    },
                    onOptionDeleted = { optionPosition ->
                        onOptionDeleted(position, optionPosition)
                        notifyDataSetChanged()
                    },
                    onAnswerKeySelected = {radioBtnPosition ->
                        onAnswerKeySelected(position,radioBtnPosition)
                        notifyItemChanged(position)
                        //notifyDataSetChanged() -> crash
                    }
                )
            }
        }
        holder.uiEtQuestion.addTextChangedListener { questionText ->
            question.text = questionText.toString()
            onQuestionTextEntered(position, questionText.toString())
            //notifyDataSetChanged() -> crash
        }
        if(question.image?.isEmpty() == true) {
            holder.uiIvDisplayImageQuestion.visibility = View.GONE
        } else {
            holder.uiIvDisplayImageQuestion.setImageURI(question.image?.toUri())
        }
        //notifyDataSetChanged() -> crash

    }

    override fun getItemCount(): Int {
        return questionList.size
    }

//    fun addSurpriseQuiz(list: List<Question>) {
//        list.let {
//            questionList.clear()
//            this.questionList.addAll(it)
//            notifyDataSetChanged()
//        }
//    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uiEtQuestion: EditText = itemView.findViewById(R.id.uiEtQuestion)
        val uiRvOptions: RecyclerView = itemView.findViewById(R.id.uiRvOptions)
        private val uiBtnAddOption: Button = itemView.findViewById(R.id.uiBtnAddOptions)
        private val uiIvAddQuestion: ImageView = itemView.findViewById(R.id.uiIvAddQuestion)
        private val uiIvCopyQuestion: ImageView = itemView.findViewById(R.id.uiIvCopyQuestion)
        private val uiIvDeleteQuestion: ImageView = itemView.findViewById(R.id.uiIvDeleteQuestion)
        private val uiTvSetAnswerKey: TextView = itemView.findViewById(R.id.uiTvSetAnswerKey)
        private val uiIvSetImageQuestion: ImageView = itemView.findViewById(R.id.uiIvSetQuestionImage)
        val uiIvDisplayImageQuestion: ImageView = itemView.findViewById(R.id.uiIvDisplayQuestionImage)

        init {
            uiIvAddQuestion.setOnClickListener {
                onAddQuestion()
                notifyDataSetChanged()
            }
            uiIvDeleteQuestion.setOnClickListener {
                onDeleteQuestion(adapterPosition)
                notifyDataSetChanged()
            }
            uiIvCopyQuestion.setOnClickListener {
                onCopyQuestion(adapterPosition,questionList[adapterPosition])
                notifyDataSetChanged()
            }
            uiBtnAddOption.setOnClickListener {
                onOptionAddButtonClicked(adapterPosition)
                notifyDataSetChanged()
            }
            uiTvSetAnswerKey.setOnClickListener {
                onSetAnswerKeyClicked(adapterPosition)
                notifyDataSetChanged()
            }
            uiIvSetImageQuestion.setOnClickListener {
                onImageQuestionClicked(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }
}