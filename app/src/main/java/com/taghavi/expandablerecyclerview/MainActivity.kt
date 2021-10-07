package com.taghavi.expandablerecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.taghavi.expandablerecyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding) // or _binding!!
    private lateinit var expandableAdapter: ExpandableCategoryAdapter
    private val expandableItems = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        for (i in 1..5) {
            val parent = Parent("Parent sequence $i")
            val childItems = arrayListOf<Child>()
            for (j in 1..5) {
                childItems.add(Child(parent, j, "Child sequence $j"))
            }
            parent.childItems.addAll(childItems)
            expandableItems.add(parent)
        }
        expandableAdapter = ExpandableCategoryAdapter(this, expandableItems)
        binding.expandableRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expandableAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}